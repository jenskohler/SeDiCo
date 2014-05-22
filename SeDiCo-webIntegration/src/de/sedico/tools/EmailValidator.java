package de.sedico.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.faces.validator.FacesValidator;
/**
 * Diese Klasse prüft, ob die Email valide ist. Sie implementiert das Interface Validator.
 * @author jens
 *
 */
@FacesValidator("de.sedico.tools.EmailValidator")
public class EmailValidator implements Validator{
	 
	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\." +
			"[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*" +
			"(\\.[A-Za-z]{2,})$";
 
	private Pattern pattern;
	private Matcher matcher;
 
	public EmailValidator(){
		  pattern = Pattern.compile(EMAIL_PATTERN);
	}
	/**
	 * Diese Methode prüft, ob die Eingabe der E-mail-Adresse korrekt ist.
	 * @param context - FacesContext
	 * @param component - UIComponent
	 * @param value - Object
	 * @throws ValidatorException - falls die Validator fehlschlägt
	 */
	@Override
	public void validate(FacesContext context, UIComponent component,
			Object value) throws ValidatorException {
 
		matcher = pattern.matcher(value.toString());
		if(!matcher.matches()){
 
			FacesMessage msg = 
				new FacesMessage("E-mail validation failed.", 
						"Invalid E-mail format.");
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(msg);
 
		}
 
	}

	
}
