package com.axxessio.oauth2.server.controller.to;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GenericTO {
	ObjectMapper mapper = new ObjectMapper();
	
	public String toJSON () throws JsonProcessingException {
		return mapper.writeValueAsString(this);
	}
}
