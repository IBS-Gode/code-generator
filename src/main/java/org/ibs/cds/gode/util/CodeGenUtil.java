package org.ibs.cds.gode.util;

import org.apache.commons.collections4.CollectionUtils;
import org.ibs.cds.gode.utils.JavaArtifact;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CodeGenUtil {

    public static String toJavaArg(List<JavaArtifact> artifactList){
        return CollectionUtils.isEmpty(artifactList) ? StringUtils.EMPTY : IntStream.range(0, artifactList.size())
                .mapToObj(k-> artifactList.get(k).fqn().concat(" ").concat("arg").concat(String.valueOf(k)))
                .collect(Collectors.joining(","));
    }

    public static String toJavaArgType(List<JavaArtifact> artifactList){
        return CollectionUtils.isEmpty(artifactList) ? StringUtils.EMPTY : IntStream.range(0, artifactList.size())
                .mapToObj(k-> "arg".concat(String.valueOf(k)))
                .collect(Collectors.joining(","));
    }

    public static String toJoinedString(List<String> artifactList){
        return CollectionUtils.isEmpty(artifactList) ? null : IntStream.range(0, artifactList.size())
                .mapToObj(k-> artifactList.get(k).concat(" ").concat("arg").concat(String.valueOf(k)))
                .collect(Collectors.joining(","));
    }
}
