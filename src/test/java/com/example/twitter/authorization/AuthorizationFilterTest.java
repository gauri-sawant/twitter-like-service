package com.example.twitter.authorization;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.container.ContainerRequestContext;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AuthorizationFilterTest {

	private static final String HEADER = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1dCI6Ik1HWmlObUU1WVdaaE5qVmpOekUxTVdJMllqUmtPVGczWkRaaE1URmpPR05oT1Roa05tRTRZUSJ9.eyJzdWIiOiJwZ2Fpa0BuZXRzLmV1IiwiaHR0cDpcL1wvd3NvMi5vcmdcL2NsYWltc1wvYXBwbGljYXRpb250aWVyIjoiVW5saW1pdGVkIiwiaHR0cDpcL1wvd3NvMi5vcmdcL2NsYWltc1wva2V5dHlwZSI6IlBST0RVQ1RJT04iLCJodHRwOlwvXC93c28yLm9yZ1wvY2xhaW1zXC92ZXJzaW9uIjoiMS4yIiwiaXNzIjoid3NvMi5vcmdcL3Byb2R1Y3RzXC9hbSIsImh0dHA6XC9cL3dzbzIub3JnXC9jbGFpbXNcL2FwcGxpY2F0aW9ubmFtZSI6Ik5BQS1BZG1pbi1VSSIsImh0dHA6XC9cL3dzbzIub3JnXC9jbGFpbXNcL2VuZHVzZXIiOiJwZ2Fpa0BuZXRzLmV1QGNhcmJvbi5zdXBlciIsImh0dHA6XC9cL3dzbzIub3JnXC9jbGFpbXNcL2VuZHVzZXJUZW5hbnRJZCI6Ii0xMjM0IiwiaHR0cDpcL1wvd3NvMi5vcmdcL2NsYWltc1wvYXBwbGljYXRpb25VVUlkIjoiMTc0NmM2MjctYmM5NS00MTEzLTg4ZjAtOWU1Y2IwM2M2ZjUyIiwiaHR0cDpcL1wvd3NvMi5vcmdcL2NsYWltc1wvc3Vic2NyaWJlciI6Ik5FVFMuRVVcL2lsb3JpQG5ldHMuZXUiLCJodHRwOlwvXC93c28yLm9yZ1wvY2xhaW1zXC90aWVyIjoiR29sZCIsImV4cCI6MTU1NzMxMDYxMSwiaHR0cDpcL1wvd3NvMi5vcmdcL2NsYWltc1wvYXBwbGljYXRpb25pZCI6IjEzNjEiLCJodHRwOlwvXC93c28yLm9yZ1wvY2xhaW1zXC91c2VydHlwZSI6IkFQUExJQ0FUSU9OX1VTRVIiLCJNdWx0aUF0dHJpYnV0ZVNlcGFyYXRvciI6W10sImVtYWlsIjoicGdhaWtAbmV0cy5ldSIsImh0dHA6XC9cL3dzbzIub3JnXC9jbGFpbXNcL2FwaWNvbnRleHQiOiJcL21zXC9hYXBheVwvcmVwb3J0aW5nXC8xLjIifQ==.NBZG8Yea0DCU1gLKYXFQC15ThfGLoQ5Jfhc290hoHD9Umjs3OqB8GmZUkfS8zjFMgfj7rD8b9G8Z1Ytnduox3d+uzi3sfisbFidw2T4pXm1j/J+RVoehe5K4unISnhtedzAokpJsUlKa6HHUzu8mREF2XKNzhNiSP/8nsU7uyysSpfQbu7AancVOMAL6P2zBGld+UcRz0vQlWigBTJr1N3XGyIU54FvRCu15JT+SLYX6jDH6w80BAohj/lay/FzBz+cZxOlSftc2/KFQyR5ZM4rbHDkacFXKeawgla1Odztm+83gknr2zhDxFhao1kmmeM6LGMi3NcEtizUAiwzAUrhQNRCSEwv4sN1Up2IAiSlDJu4ttZ2J+Y6LjIKaA7nELcTJcQyPXeG70sgt3IKnMjujrOrw6D6N5eLrZ9J9+tIgb1YnmafqpvpdysF09bI01vo8K7qggKFy745a412jFGhVikgf8E4+yrOlk8eHZXM7UZrxJOBzO4izQeevtEIhvZ/EyYmFbGjnvh1szNaCvwICZvihbsEbz+rp3uR020JJJoy5pNh2alcGwMyL7r4cVZksuJ10xGhgOwn7aCMjxj/nIPcYCFmJ7uwtqckwol1s00w0nZJ352OVlAH8xKLlOsFN5jAvh0oevnMWKRgm7s9WuHKGXv0nAuAq8Y7ZvpI=";
	
	@Mock
	private ContainerRequestContext context;

	@InjectMocks
	private AuthorizationFilter filter = new AuthorizationFilter();

	@Test
	public void shouldValidate() {
		when(context.getHeaderString(anyString())).thenReturn(HEADER);
		filter.filter(context);
		verify(context,times(0)).abortWith(any());
	}

	@Test
	public void shouldReturnWithUnauthorizedForHeader() {
		when(context.getHeaderString(anyString())).thenReturn("1"+HEADER);
		filter.filter(context);
		verify(context).abortWith(any());
	}

	@Test
	public void shouldReturnWithUnauthorizedForWrongSignature() {
		when(context.getHeaderString(anyString())).thenReturn(HEADER.substring(0, HEADER.length()-2)+"A=");
		filter.filter(context);
		verify(context).abortWith(any());
	}

	@Test
	public void shouldReturnWithUnauthorizedForIncorectEncoding() {
		when(context.getHeaderString(anyString())).thenReturn(HEADER+"A");
		filter.filter(context);
		verify(context).abortWith(any());
	}

}
