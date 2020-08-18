package org.ibs.cds.gode.codegenerator.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DockerComposeConfigration {
    public String version;
    public Map<String, DockerServiceConfigration> services;
    public Map<String, Map<String,String>> networks;
    public Map<String, Map<String,String>> volumes;

}
