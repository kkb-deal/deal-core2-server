package cn.deal.core.customer.engine.validators;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

import java.net.IDN;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

/**
 * 邮箱格式校验器
 */
@Component
public class EmailValidator extends AbstractValidator {
	
	private static final String ATOM = "[a-z0-9!#$%&'*+/=?^_`{|}~-]";
	private static final String DOMAIN = ATOM + "+(\\." + ATOM + "+)*";
	private static final String IP_DOMAIN = "\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\]";
	private static final int MAX_LOCAL_PART_LENGTH = 64;
	private static final int MAX_DOMAIN_PART_LENGTH = 255;

	/**
	 * Regular expression for the local part of an email address (everything before '@')
	 */
	private final Pattern localPattern = java.util.regex.Pattern.compile(
			ATOM + "+(\\." + ATOM + "+)*", CASE_INSENSITIVE
	);

	/**
	 * Regular expression for the domain part of an email address (everything after '@')
	 */
	private final Pattern domainPattern = java.util.regex.Pattern.compile(
			DOMAIN + "|" + IP_DOMAIN, CASE_INSENSITIVE
	);

	@Override
	public boolean isValid(String value) {
		if ( value == null || value.length() == 0 ) {
			return true;
		}

		// split email at '@' and consider local and domain part separately;
		// note a split limit of 3 is used as it causes all characters following to an (illegal) second @ character to
		// be put into a separate array element, avoiding the regex application in this case since the resulting array
		// has more than 2 elements
		String[] emailParts = value.toString().split( "@", 3 );
		if ( emailParts.length != 2 ) {
			return false;
		}

		// if we have a trailing dot in local or domain part we have an invalid email address.
		// the regular expression match would take care of this, but IDN.toASCII drops the trailing '.'
		// (imo a bug in the implementation)
		if ( emailParts[0].endsWith( "." ) || emailParts[1].endsWith( "." ) ) {
			return false;
		}

		if ( !matchPart( emailParts[0], localPattern, MAX_LOCAL_PART_LENGTH ) ) {
			return false;
		}

		return matchPart( emailParts[1], domainPattern, MAX_DOMAIN_PART_LENGTH );
	}

	private boolean matchPart(String part, Pattern pattern, int maxLength) {
		String asciiString;
		try {
			asciiString = toAscii( part );
		}
		catch ( IllegalArgumentException e ) {
			return false;
		}

		if ( asciiString.length() > maxLength ) {
			return false;
		}

		Matcher matcher = pattern.matcher( asciiString );
		return matcher.matches();
	}

	private String toAscii(String unicodeString) throws IllegalArgumentException {
		String asciiString = "";
		int start = 0;
		int end = unicodeString.length() <= 63 ? unicodeString.length() : 63;
		while ( true ) {
			// IDN.toASCII only supports a max "label" length of 63 characters. Need to chunk the input in these sizes
			asciiString += IDN.toASCII( unicodeString.substring( start, end ) );
			if ( end == unicodeString.length() ) {
				break;
			}
			start = end;
			end = start + 63 > unicodeString.length() ? unicodeString.length() : start + 63;
		}

		return asciiString;
	}

}
