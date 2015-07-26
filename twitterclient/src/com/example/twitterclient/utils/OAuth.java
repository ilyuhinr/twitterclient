package com.example.twitterclient.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OAuth {

	public static final String VERSION_1_0 = "1.0";
	public static final String ENCODING = "UTF-8";

	private static String characterEncoding = ENCODING;

	public static void setCharacterEncoding(String encoding) {
		OAuth.characterEncoding = encoding;
	}

	public static String decodeCharacters(byte[] from) {
		if (characterEncoding != null) {
			try {
				return new String(from, characterEncoding);
			} catch (UnsupportedEncodingException e) {
				System.err.println(e + "");
			}
		}
		return new String(from);
	}

	public static byte[] encodeCharacters(String from) {
		if (characterEncoding != null) {
			try {
				return from.getBytes(characterEncoding);
			} catch (UnsupportedEncodingException e) {
				System.err.println(e + "");
			}
		}
		return from.getBytes();
	}

	public static String formEncode(Iterable<? extends Map.Entry> parameters)
			throws IOException {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		formEncode(parameters, b);
		return decodeCharacters(b.toByteArray());
	}

	public static void formEncode(Iterable<? extends Map.Entry> parameters,
			OutputStream into) throws IOException {
		if (parameters != null) {
			boolean first = true;
			for (Map.Entry parameter : parameters) {
				if (first) {
					first = false;
				} else {
					into.write('&');
				}
				into.write(encodeCharacters(percentEncode(toString(parameter
						.getKey()))));
				into.write('=');
				into.write(encodeCharacters(percentEncode(toString(parameter
						.getValue()))));
			}
		}
	}

	public static List<Parameter> decodeForm(String form) {
		List<Parameter> list = new ArrayList<Parameter>();
		if (!isEmpty(form)) {
			for (String nvp : form.split("\\&")) {
				int equals = nvp.indexOf('=');
				String name;
				String value;
				if (equals < 0) {
					name = decodePercent(nvp);
					value = null;
				} else {
					name = decodePercent(nvp.substring(0, equals));
					value = decodePercent(nvp.substring(equals + 1));
				}
				list.add(new Parameter(name, value));
			}
		}
		return list;
	}

	public static String percentEncode(Iterable values) {
		StringBuilder p = new StringBuilder();
		for (Object v : values) {
			if (p.length() > 0) {
				p.append("&");
			}
			p.append(OAuth.percentEncode(toString(v)));
		}
		return p.toString();
	}

	public static String percentEncode(String s) {
		if (s == null) {
			return "";
		}
		try {
			return URLEncoder.encode(s, ENCODING)
					// OAuth encodes some characters differently:
					.replace("+", "%20").replace("*", "%2A")
					.replace("%7E", "~");
			// This could be done faster with more hand-crafted code.
		} catch (UnsupportedEncodingException wow) {
			throw new RuntimeException(wow.getMessage(), wow);
		}
	}

	public static String decodePercent(String s) {
		try {
			return URLDecoder.decode(s, ENCODING);
			// This implements http://oauth.pbwiki.com/FlexibleDecoding
		} catch (java.io.UnsupportedEncodingException wow) {
			throw new RuntimeException(wow.getMessage(), wow);
		}
	}

	public static Map<String, String> newMap(Iterable<? extends Map.Entry> from) {
		Map<String, String> map = new HashMap<String, String>();
		if (from != null) {
			for (Map.Entry f : from) {
				String key = toString(f.getKey());
				if (!map.containsKey(key)) {
					map.put(key, toString(f.getValue()));
				}
			}
		}
		return map;
	}

	/**
	 * Construct a list of Parameters from name, value, name, value...
	 */
	public static List<Parameter> newList(String... parameters) {
		List<Parameter> list = new ArrayList<Parameter>(parameters.length / 2);
		for (int p = 0; p + 1 < parameters.length; p += 2) {
			list.add(new Parameter(parameters[p], parameters[p + 1]));
		}
		return list;
	}

	/**
	 * A name/value pair.
	 */
	public static class Parameter implements Map.Entry<String, String> {

		public Parameter(String key, String value) {
			this.key = key;
			this.value = value;
		}

		private final String key;

		private String value;

		public String getKey() {
			return key;
		}

		public String getValue() {
			return value;
		}

		public String setValue(String value) {
			try {
				return this.value;
			} finally {
				this.value = value;
			}
		}

		@Override
		public String toString() {
			return percentEncode(getKey()) + '=' + percentEncode(getValue());
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((key == null) ? 0 : key.hashCode());
			result = prime * result + ((value == null) ? 0 : value.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final Parameter that = (Parameter) obj;
			if (key == null) {
				if (that.key != null)
					return false;
			} else if (!key.equals(that.key))
				return false;
			if (value == null) {
				if (that.value != null)
					return false;
			} else if (!value.equals(that.value))
				return false;
			return true;
		}
	}

	private static final String toString(Object from) {
		return (from == null) ? null : from.toString();
	}

	public static String addParameters(String url, String... parameters)
			throws IOException {
		return addParameters(url, newList(parameters));
	}

	public static String addParameters(String url,
			Iterable<? extends Map.Entry<String, String>> parameters)
			throws IOException {
		String form = formEncode(parameters);
		if (form == null || form.length() <= 0) {
			return url;
		} else {
			return url + ((url.indexOf("?") < 0) ? '?' : '&') + form;
		}
	}

	public static boolean isEmpty(String str) {
		return (str == null) || (str.length() == 0);
	}
}
