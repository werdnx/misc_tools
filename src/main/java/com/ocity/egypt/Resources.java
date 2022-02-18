package com.ocity.egypt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Resources {
    @JacksonXmlElementWrapper(useWrapping = false, localName = "string")
    private List<ResourceItem> string = new ArrayList<>();

    public Resources() {

    }

    public Resources(List<ResourceItem> result) {
        string = result;
    }
}
