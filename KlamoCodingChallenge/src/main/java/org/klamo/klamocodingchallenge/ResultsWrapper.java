package org.klamo.klamocodingchallenge;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "parsingResults")
public class ResultsWrapper {
    @XmlElement(name = "result")
    public List<ParseResult> results;

    public ResultsWrapper(@NonNull List<ParseResult> results) {
        this.results = results;
    }

    // NOTE - This is only in here to prevent JAXB from throwing a fit over marshalling/unmarshalling this class.
    // TODO - Consider properly fixing this using XmlAdapter/@XmlJavaTypeAdapter? https://stackoverflow.com/a/9268488/3699914
    private ResultsWrapper() {
        this.results = new ArrayList<>();
    }
}
