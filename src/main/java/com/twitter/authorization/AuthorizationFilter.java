package com.twitter.authorization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Provider
@Secured
public class AuthorizationFilter implements ContainerRequestFilter {

	static final String REGEX_DOT = "\\.";

	private static final String JWT_HEADER = "x-jwt-assertion";
	private static final String EXPONENT = "AQAB";
	private static final String RSA = "RSA";

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationFilter.class);
	private static final String ALGORITHM = "SHA256withRSA";
	private static final int MIN_LENGTH_JWTDATA = 2;
	private static final int MIN_LENGTH_JWTSIGNATURE = 3;
	private static final String MIN_LENGTH_ERROR_MESSAGE = "Array index exceeds the length of array => min: %d, got: %d";
	private static final int SIGNUM = 1;


	@Override
	public void filter(ContainerRequestContext requestContext) {

		String jwt = getJWTFromHeader(requestContext);
		try {

			boolean valid = validateToken(jwt, generatePublicKey());
			if (!valid) {
				abortWithUnauthorized(requestContext);
			}

		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("No such algorithm exception", e);
			requestContext.abortWith(
					Response.status(Response.Status.INTERNAL_SERVER_ERROR)
							.build());
		} catch (InvalidKeySpecException | InvalidKeyException | IllegalArgumentException | SignatureException e) {
			LOGGER.error("Invalid JWT", e);
			abortWithUnauthorized(requestContext);
		}
	}

	private String getJWTFromHeader(ContainerRequestContext requestContext) {
		return requestContext.getHeaderString(JWT_HEADER);
	}

	private PublicKey generatePublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
		String encodedModulus = "xlgXXVfPam26K1O2iJznYQW9Tj3qPDt1rAtPTeLyDsNCJ1kbg-phHrmMkSooLJZNgSKEagn5mvZAF4JTsdtt_Og4CMbYXSfyaJ2vu24O6OCl_2zVbx3whz08S26pe4Wz7oqRwcFwiLPUgpGr5yLB3uPHDDavG2GceanwsCLWOfWX_36TwVCvw-vjDoMoMX4CoDFyDZTb7emnJCf2W0mwQEtzYbz2rvAdOYWsoFEPAOvu0bib4PnTHBd_QMLnzYvO-67rEsvnAoQbEThoV6VeHIJFaGpQvTInw2TyCbkAhb2vkWUGBfQ_qulZISUkmo4U_4FVzLljZvaHhVXhRW0XNLWlDhFOQlFSJTxlmeHXALbLSjvQFSx3O0EHIwF8QtiFZBTBazPrbfM6PIww4Rtn7He7SDW0MJ3_ErK_jFrOnI6RxY7GJFJeYxzxBuiZ-aaIAT6Lcmf1_sl-TcH-ZDBHLVfvPgUeZ9buggOzjPJb-2AByLbYcjDQ1XMMuy6u-cx8V3p7yOAMCoPLFNdHBnuWm1ayOQXZ4GirEr4D9k5xVlDSHhEpoAQMGuGpRoDMFYGNiFWFhrHE9NS-AlnUVLYyae9v6QDy1mNwln0VuFm0YnNxjGz_IQ66MrwRAnVxxwOG0AN6-D4Nk9QkXJ6R-Gt51N2m7oAndjBZ6RBG3I68iKU";

		BigInteger modulus = new BigInteger(SIGNUM, Base64.getUrlDecoder().decode(encodedModulus));
		BigInteger exponent = new BigInteger(SIGNUM, Base64.getUrlDecoder().decode(EXPONENT));

		KeyFactory keyFactory = KeyFactory.getInstance(RSA);
		PublicKey publicKey = keyFactory.generatePublic(new RSAPublicKeySpec(modulus, exponent));
		X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKey.getEncoded());

		return keyFactory.generatePublic(x509EncodedKeySpec);
	}

	private void abortWithUnauthorized(ContainerRequestContext requestContext) {

		requestContext.abortWith(
				Response.status(Response.Status.UNAUTHORIZED)
						.build());
	}

	private boolean validateToken(String jwt, PublicKey publicKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {

		if (jwt == null || jwt.isEmpty()) {
			return false;
		}

		Signature signature = Signature.getInstance(ALGORITHM);

		signature.initVerify(publicKey);
		signature.update(getJWTData(jwt));

		return signature.verify(getJWTSignature(jwt));

	}

	private byte[] getJWTData(String jwt) throws InvalidKeyException {
		String[] res = jwt.split(REGEX_DOT);
		checkArrayMinimumLength(res, MIN_LENGTH_JWTDATA);
		return (res[0] + "." + res[1]).getBytes();
	}

	private byte[] getJWTSignature(String jwt) throws InvalidKeyException {
		String[] res = jwt.split(REGEX_DOT);
		checkArrayMinimumLength(res, MIN_LENGTH_JWTSIGNATURE);
		return Base64.getDecoder().decode(res[2].getBytes(Charset.defaultCharset()));
	}

	private void checkArrayMinimumLength(String[] res, int minimumLength) throws InvalidKeyException {
		if (res.length < minimumLength) {
			throw new InvalidKeyException(String.format(MIN_LENGTH_ERROR_MESSAGE, minimumLength, res.length));
		}
	}
}