package org.klamo.klamocodingchallenge;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.NonNull;

@XmlRootElement
public class ParseResult {
    @XmlElement
    public String inputString;

    @XmlAttribute
    public Boolean isValid;

    @XmlElement
    public String errorMessage;

    public ParseResult(@NonNull String inputString, Boolean isValid, String errorMessage) {
        this.inputString = inputString;
        this.isValid = isValid;
        this.errorMessage = errorMessage;

        if (!isValid && errorMessage == null) {
            this.errorMessage = "No error message provided.";
        }
    }

    // NOTE - This is only in here to prevent JAXB from throwing a fit over marshalling/unmarshalling this class.
    // TODO - Consider properly fixing this using XmlAdapter/@XmlJavaTypeAdapter? https://stackoverflow.com/a/9268488/3699914
    private ParseResult() {
        inputString = "";
        isValid = false;
        errorMessage = "";
    }
}
