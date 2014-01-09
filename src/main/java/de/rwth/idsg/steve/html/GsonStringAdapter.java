package de.rwth.idsg.steve.html;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
* This class adds the functionality to serialize a null String object into an empty String.
* 
* @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
* 
*/
public class GsonStringAdapter extends TypeAdapter<String> {

	@Override
	public void write(JsonWriter out, String value) throws IOException {
		if (value == null) {
			out.value("");
		} else {
			out.value(value);
		}		
	}

	@Override
	public String read(JsonReader in) throws IOException {
		return in.toString();
	}

}
