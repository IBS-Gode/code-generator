package org.ibs.cds.gode.codegenerator.model.deploy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.ibs.cds.gode.codegenerator.config.CodeGenerationComponent;
import org.ibs.cds.gode.codegenerator.entity.CodeApp;
import org.ibs.cds.gode.codegenerator.entity.CodeAppUtil;
import org.ibs.cds.gode.entity.type.FieldType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.ibs.cds.gode.codegenerator.model.deploy.Action.of;

public enum DockerDeploymentRequirement implements DeploymentRequirements {
    ADMIN_PORT(always(), "Monitor app port","adminPort", FieldType.NUMBER,
            of(CodeGenerationComponent.ComponentName.ADMIN, "server.port"),
            of(CodeGenerationComponent.ComponentName.APP, "spring.boot.admin.client.url", adminUrl()),
            of(CodeGenerationComponent.ComponentName.DOCKER_COMPOSE, "adminserver.port", dockerExternalAndInternalPort(),adminDockerPortProperty())),

    JPA_URL(requireJPA(), "JPA port","jpaPort", FieldType.TEXT,
            of(CodeGenerationComponent.ComponentName.DOCKER_COMPOSE, "jpa.url",dockerJpaExternalAndInternalPort(), dockerJpaExternalAndInternalPortProperty()),
            of(CodeGenerationComponent.ComponentName.APP_MIGRATION, "url",linkBaseUrl()),
            of(CodeGenerationComponent.ComponentName.APP_MIGRATION, "password",getValueString("dbpass")),
            of(CodeGenerationComponent.ComponentName.APP_MIGRATION, "username",getValueString("dbuser")),
            of(CodeGenerationComponent.ComponentName.APP_MIGRATION, "driver",getValueString("com.mysql.cj.jdbc.Driver"))),

    APP_PORT(always(), "Application port","appPort", FieldType.NUMBER,
            of(CodeGenerationComponent.ComponentName.APP, "appserver.port"),
            of(CodeGenerationComponent.ComponentName.DOCKER_COMPOSE, "server.port", dockerAppExternalAndInternalPort(), appDockerPortProperty())),

    MEDIA_SERVER_LOC(always(), "Media server directory","mediaServer", FieldType.TEXT,
            of(CodeGenerationComponent.ComponentName.APP, "gode.media.store.location")),

    QUEUE_PREFIX(requireQueueServer(), "Prefix for queue name","queuePrefix", FieldType.TEXT,
            of(CodeGenerationComponent.ComponentName.APP, "gode.queue.context.prefix")),

    QUEUE_SERVER(requireQueueServer(), "Queue servers","queueServer", FieldType.TEXT,
            of(CodeGenerationComponent.ComponentName.APP, "gode.queue.kafka.servers")),

    GENERAL_QUEUE(requireQueueServer(), "System queue name","generalQueue", FieldType.TEXT,
            of(CodeGenerationComponent.ComponentName.APP, "gode.queue.general")),

    QUEUE_GROUP_ID(requireQueueServer(), "System queue message group","queueGroupId", FieldType.TEXT,
            of(CodeGenerationComponent.ComponentName.APP, "gode.queue.kafka.groupId")),

    QUEUE_SECURITY(requireQueueServer(), "Queue Security","queueSecurity", FieldType.BOOLEAN,
            of(CodeGenerationComponent.ComponentName.APP, "gode.queue.kafka.security.sasl")),

    QUEUE_SECURITY_MECHANISM(requireQueueServer(), "Queue Security Mechanism","queueSecurityMechanism", FieldType.TEXT,
            of(CodeGenerationComponent.ComponentName.APP, "gode.queue.kafka.security.mechanism")),

    QUEUE_SECURITY_MECHANISM_JAAS(requireQueueServer(), "Queue Security Configuration","queueSecurityMechanismJaas", FieldType.TEXT,
            of(CodeGenerationComponent.ComponentName.APP, "gode.queue.kafka.security.jaas")),

    MAIL_SMTP_SERVER(c -> true, "SMTP Mail Server","smtpServer", FieldType.TEXT,
            of(CodeGenerationComponent.ComponentName.ADMIN, "spring.mail.host")),

    MAIL_SMTP_PORT(c -> true, "SMTP Mail Port","smtpPort", FieldType.NUMBER,
            of(CodeGenerationComponent.ComponentName.ADMIN, "spring.mail.port")),

    MAIL_USERNAME(c -> true, "SMTP Mail Username","mailUsername", FieldType.TEXT,
            of(CodeGenerationComponent.ComponentName.ADMIN, "spring.mail.username")),

    MAIL_PASSWORD(c -> true, "SMTP Mail Password","mailPassword", FieldType.TEXT,
            of(CodeGenerationComponent.ComponentName.ADMIN, "spring.mail.password")),

    MAIL_NOTIFICATION_ENABLE(c -> true, "Mail Notification Enable","mailNotifyEnable", FieldType.BOOLEAN,
            of(CodeGenerationComponent.ComponentName.ADMIN, "spring.boot.admin.notify.mail.enabled")),

    MAIL_NOTIFICATION_SENDER(c -> true, "Mail Notification Sender","mailSender", FieldType.TEXT,
            of(CodeGenerationComponent.ComponentName.ADMIN, "spring.boot.admin.notify.mail.from")),

    MAIL_NOTIFICATION_RECEIVER(c -> true, "Mail Notification Receiver","mailReceiver", FieldType.TEXT,
            of(CodeGenerationComponent.ComponentName.ADMIN, "spring.boot.admin.notify.mail.to")),

    SLACK_NOTIFICATION_ENABLE(c -> true, "Teams Notification Enable","teamsNotifyEnable", FieldType.BOOLEAN,
            of(CodeGenerationComponent.ComponentName.ADMIN, "spring.boot.admin.notify.ms-teams.enabled")),

    SLACK_NOTIFICATION_WEBHOOKS_URL(c -> true, "Teams Notification Webhooks Url","teamsWebhooksUrl", FieldType.TEXT,
            of(CodeGenerationComponent.ComponentName.ADMIN, "spring.boot.admin.notify.ms-teams.webhook-url")),

    TEAMS_NOTIFICATION_ENABLE(c -> true, "Slack Notification Enable","slackNotifyEnable", FieldType.BOOLEAN,
            of(CodeGenerationComponent.ComponentName.ADMIN, "spring.boot.admin.notify.slack.enabled")),

    TEAMS_NOTIFICATION_WEBHOOKS_URL(c -> true, "Slack Notification Webhooks Url","slackWebhooksUrl", FieldType.TEXT,
            of(CodeGenerationComponent.ComponentName.ADMIN, "spring.boot.admin.notify.slack.webhook-url")),

    ;

    private static Function<DockerDeploymentRequirement, Function<CodeApp, String>> migrationSearch() {
        return  requirement -> codeApp -> "hibernate:spring:org.ibs.cds.gode.entity.type?dialect=".concat(requirement.getValue());
    }

    private final Predicate<CodeApp> entryCriteria;
    private @Getter
    final String propertyName;
    private @Getter
    final FieldType type;
    private @Getter
    @Setter
    String value;
    private @Getter
    List<Action> actions;
    private @Getter String label;
    DockerDeploymentRequirement(Predicate<CodeApp> entryCriteria, String label, String propertyName, FieldType type, Action... actions) {
        this.entryCriteria = entryCriteria;
        this.propertyName = propertyName;
        this.type = type;
        this.label = label;
        this.actions = List.of(actions);
    }

    @NotNull
    private static Function<org.ibs.cds.gode.codegenerator.model.deploy.DeploymentRequirements, Function<CodeApp, String>> adminUrl() {
        return req -> codeApp -> "http://".concat(CodeAppUtil.adminAppName(codeApp).toLowerCase()).concat("-service:").concat(req.getValue()).concat("/").concat(CodeAppUtil.adminAppName(codeApp).toLowerCase());
    }

    @NotNull
    private static Function<org.ibs.cds.gode.codegenerator.model.deploy.DeploymentRequirements, Function<CodeApp, String>> dockerExternalAndInternalPort() {
        return req -> codeApp -> req.getValue().concat(":").concat(req.getValue());
    }
    @NotNull
    private static Function<org.ibs.cds.gode.codegenerator.model.deploy.DeploymentRequirements, Function<CodeApp, String>> linkBaseUrl() {
        return req -> codeApp -> "jdbc:mysql://jpa-mySql-database_".concat(codeApp.getName().toLowerCase()).concat("/").concat(codeApp.getName().toLowerCase()).concat("_db?useSSL=false&allowPublicKeyRetrieval=true");
    }

    @NotNull
    private static Function<org.ibs.cds.gode.codegenerator.model.deploy.DeploymentRequirements, Function<CodeApp, String>> getValueString(String value) {
        return req -> codeApp -> value;
    }

    @NotNull
    private static Function<org.ibs.cds.gode.codegenerator.model.deploy.DeploymentRequirements, Function<CodeApp, String>> dockerJpaExternalAndInternalPort() {
        return req -> codeApp -> req.getValue().concat(":").concat(req.getValue());
    }

    @NotNull
    private static Function<org.ibs.cds.gode.codegenerator.model.deploy.DeploymentRequirements, Function<CodeApp, String>> dockerJpaExternalAndInternalPortProperty() {
        return req -> codeApp ->  "services(jpa-mySql-database_".concat(codeApp.getName().toLowerCase()).concat(")").concat(".ports[0]");
    }

    @NotNull
    private static Function<org.ibs.cds.gode.codegenerator.model.deploy.DeploymentRequirements, Function<CodeApp, String>> dockerAppExternalAndInternalPort() {
        return req -> codeApp -> req.getValue().concat(":").concat(req.getValue());
    }

    @NotNull
    private static Function<org.ibs.cds.gode.codegenerator.model.deploy.DeploymentRequirements, Function<CodeApp, String>> adminDockerPortProperty() {
        return req -> codeApp ->  "services(".concat(CodeAppUtil.adminAppName(codeApp).toLowerCase()).concat("-service)").concat(".ports[0]");
    }
    @NotNull
    private static Function<org.ibs.cds.gode.codegenerator.model.deploy.DeploymentRequirements, Function<CodeApp, String>> appDockerPortProperty() {
        return req -> codeApp ->  "services(".concat(codeApp.getName().toLowerCase()).concat("-service).ports[0]");
    }

    @NotNull
    private static Predicate<CodeApp> requireJPA() {
        return c -> c.getFeatures().isJpaStoreRequired();
    }

    @NotNull
    private static Predicate<CodeApp> requireMongoDB() {
        return c -> c.getFeatures().isMongoRequired();
    }

    @NotNull
    private static Predicate<CodeApp> requireQueueServer() {
        return c -> c.getFeatures().isQueueSystemRequired();
    }

    @NotNull
    private static Predicate<CodeApp> always() {
        return c -> true;
    }

    @JsonIgnore
    public static DockerDeploymentRequirement from(String propertyName, String value, CodeApp app) {
        return Stream.of(DockerDeploymentRequirement.values())
                .filter(k -> k.propertyName.equals(propertyName))
                .findAny()
                .map(k -> {
                    k.setValue(value);
                    return k;
                })
                .orElse(null);
    }

    @JsonIgnore
    public static Map<String, String> values(CodeApp app) {
        return Arrays
                .stream(DockerDeploymentRequirement.values())
                .filter(k -> k.entryCriteria.test(app))
                .collect(Collectors.toMap(s -> s.propertyName, s -> s.getLabel()));
    }

    @NotNull
    private static Predicate<CodeApp> requireCassandra() {
        return c -> c.getFeatures().isCassandraRequired();
    }

    @Override
    public String toString() {
        return this.propertyName;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getPropertyName() {
        return propertyName;
    }
}
