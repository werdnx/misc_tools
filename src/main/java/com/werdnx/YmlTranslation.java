package com.werdnx;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.net.URI;
import java.util.Map;


/*
        let headers = [
            "content-type": "application/x-www-form-urlencoded",
            "x-rapidapi-key": "46716022b4msh875095342c6bffbp165157jsn9787920ef0ec",
            "x-rapidapi-host": "nlp-translation.p.rapidapi.com"
        ]

        let textForTranslate = replaceSymbols(text)

        let postData = NSMutableData(data: "text=\(textForTranslate)".data(using: String.Encoding.utf8)!)
        postData.append("&to=\(targetLang)".data(using: String.Encoding.utf8)!)
        postData.append("&from=\(sourceLang)".data(using: String.Encoding.utf8)!)

        let request = NSMutableURLRequest(
            url: NSURL(string: "https://nlp-translation.p.rapidapi.com/v1/translate")! as URL,
            cachePolicy: .useProtocolCachePolicy,
            timeoutInterval: 9999)
        request.httpMethod = "POST"
        request.allHTTPHeaderFields = headers
        request.httpBody = postData as Data


https://rapidapi.com/gofitech/api/nlp-translation
 */

public class YmlTranslation {
    public static void main(String[] args) throws FileNotFoundException {
        new YmlTranslation().translate();
    }

    private void translate() throws FileNotFoundException {
        WebClient client = WebClient.builder()
                .baseUrl("https://nlp-translation.p.rapidapi.com/v1")
                .defaultCookie("cookieKey", "cookieValue")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .defaultHeader("x-rapidapi-key", "46716022b4msh875095342c6bffbp165157jsn9787920ef0ec")
                .defaultHeader("x-rapidapi-host", "nlp-translation.p.rapidapi.com")
                .build();
        WebClient.UriSpec<WebClient.RequestBodySpec> uriSpec = client.method(HttpMethod.POST);
        WebClient.RequestBodySpec bodySpec = uriSpec.uri(URI.create("https://nlp-translation.p.rapidapi.com/v1/translate"));

        Map<Object, Object> ymlMap = prepareText();
        translateMap(ymlMap, bodySpec);
        PrintWriter writer = new PrintWriter("es.yml");
        DumperOptions options = new DumperOptions();
        options.setIndent(2);
        options.setPrettyFlow(true);
        // Fix below - additional configuration
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(options);
        yaml.dump(ymlMap, writer);
    }

    private void translateMap(Map<Object, Object> map, WebClient.RequestBodySpec bodySpec) {
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof String || entry.getValue() instanceof Boolean) {
                String sb = "text=" +
                        entry.getValue() +
                        "&to=es&from=en";
                WebClient.RequestHeadersSpec<?> headersSpec = bodySpec.bodyValue(sb);
                //signup -> {"status":200,"from":"en","to":"es","original_text":"Sign UP","translated_text":{"es":"Inscribirse"},"translated_characters":7}
                Resp resp = headersSpec.retrieve().bodyToMono(Resp.class).block();
                System.out.println(resp.getTranslated().getEs());
                map.put(entry.getKey(), resp.getTranslated().getEs());
            } else if (entry.getValue() instanceof Map) {
                translateMap((Map<Object, Object>) entry.getValue(), bodySpec);
            } else {
                System.out.println(entry.getValue().getClass());
                System.out.println(entry.getValue());
                System.out.println(map);
                throw new IllegalStateException("AXTUNG!!!");
            }
        }
    }

    private Map<Object, Object> prepareText() throws FileNotFoundException {
        InputStream inputStream = new FileInputStream(new File("C:\\develop\\misc_tools\\src\\main\\resources\\en.yaml"));

        Yaml yaml = new Yaml();
        return yaml.load(inputStream);
    }

    @Data
    public static class Resp {
        Integer status;
        String from;
        String to;
        String original_text;
        @JsonProperty("translated_text")
        Trans translated;
        Integer translated_characters;

    }

    @Data
    public static class Trans {
        String es;
    }
}
