package org.ibs.cds.gode.codegenerator.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DockerServiceConfigration {
    public String image;
    public List<String> volumes;
    public String restart;
    public List<String> ports;
    public List<String> depends_on;
    public List<String> environment;
    public List<String> networks;


}
