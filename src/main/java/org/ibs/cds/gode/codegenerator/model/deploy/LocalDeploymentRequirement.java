package org.ibs.cds.gode.codegenerator.model.deploy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.ibs.cds.gode.codegenerator.config.CodeGenerationComponent;
import org.ibs.cds.gode.codegenerator.entity.CodeApp;
import org.ibs.cds.gode.codegenerator.entity.CodeAppUtil;
import org.ibs.cds.gode.entity.store.StoreType;
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

public enum LocalDeploymentRequirement {
    ADMIN_PORT(c -> true, "Monitor app port","adminPort", FieldType.NUMBER,
            of(CodeGenerationComponent.ComponentName.ADMIN, "server.port"),
            of(CodeGenerationComponent.ComponentName.APP, "spring.boot.admin.client.url", adminUrl())),

    JPA_DRIVER(requireJPA(), "JPA Driver","jpaDriver", FieldType.TEXT,
            of(CodeGenerationComponent.ComponentName.APP, "gode.datastore.jpa.driver")),

    JPA_DIALECT(requireJPA(), "JPA Dialect","jpaDialect", FieldType.TEXT,
            of(CodeGenerationComponent.ComponentName.APP, "gode.datastore.jpa.dialect")),

    JPA_URL(requireJPA(), "JPA Database url","jpaUrl", FieldType.TEXT,
            of(CodeGenerationComponent.ComponentName.APP, "gode.datastore.jpa.datasource.url")),

    JPA_USERNAME(requireJPA(), "JPA Database username", "jpaUsername", FieldType.TEXT,
            of(CodeGenerationComponent.ComponentName.APP, "gode.datastore.jpa.datasource.username")),

    JPA_PASSWORD(requireJPA(), "JPA Database password","jpaPassword", FieldType.TEXT,
            of(CodeGenerationComponent.ComponentName.APP, "gode.datastore.jpa.datasource.password")),

    MONGODB_URI(requireMongoDB(), "MongoDB URI","mongoUri", FieldType.TEXT,
            of(CodeGenerationComponent.ComponentName.APP, "gode.datastore.mongodb.uri")),

    MONGODB_DATABASE(requireMongoDB(), "MongoDB useername","mongoDatabaseName", FieldType.TEXT,
            of(CodeGenerationComponent.ComponentName.APP, "gode.datastore.mongodb.database.name")),

    CASSANDRA_PORT(requireCassandra(), "Cassandra port","cassandraPort", FieldType.TEXT,
            of(CodeGenerationComponent.ComponentName.APP, "spring.data.cassandra.port")),

    CASSANDRA_USERNAME(requireCassandra(), "Cassandra username","cassandraUsername", FieldType.TEXT,
            of(CodeGenerationComponent.ComponentName.APP, "spring.data.cassandra.username")),

    CASSANDRA_PASSWORD(requireCassandra(), "Cassandra username","cassandraPassword", FieldType.TEXT,
            of(CodeGenerationComponent.ComponentName.APP, "spring.data.cassandra.password")),

    CASSANDRA_URI(requireCassandra(), "Cassandra URI", "cassandraContactPoint", FieldType.TEXT,
            of(CodeGenerationComponent.ComponentName.APP, "spring.data.cassandra.contact-points")),

    APP_PORT(c -> true, "Application port","appPort", FieldType.NUMBER,
            of(CodeGenerationComponent.ComponentName.APP, "server.port")),

    MEDIA_SERVER_LOC(c -> true, "Media server directory","mediaServer", FieldType.TEXT,
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

    SLACK_NOTIFICATION_ENABLE(c -> true, "Slack Notification Enable","slackNotifyEnable", FieldType.BOOLEAN,
            of(CodeGenerationComponent.ComponentName.ADMIN, "spring.boot.admin.notify.ms-teams.enabled")),

    SLACK_NOTIFICATION_WEBHOOKS_URL(c -> true, "Slack Notification Webhooks Url","slackWebhooksUrl", FieldType.TEXT,
            of(CodeGenerationComponent.ComponentName.ADMIN, "spring.boot.admin.notify.ms-teams.webhook-url")),

    TEAMS_NOTIFICATION_ENABLE(c -> true, "Teams Notification Enable","teamsNotifyEnable", FieldType.BOOLEAN,
            of(CodeGenerationComponent.ComponentName.ADMIN, "spring.boot.admin.notify.slack.enabled")),

    TEAMS_NOTIFICATION_WEBHOOKS_URL(c -> true, "Teams Notification Webhooks Url","teamsWebhooksUrl", FieldType.TEXT,
            of(CodeGenerationComponent.ComponentName.ADMIN, "spring.boot.admin.notify.slack.webhook-url")),

    ;

    private final Predicate<CodeApp> entryCriteria;
    private @Getter
    final String propertyName;
    private @Getter
    final FieldType type;
    private @Getter
    @Setter
    String value;
    private @Getter List<Action> actions;
    private @Getter String label;
    LocalDeploymentRequirement(Predicate<CodeApp> entryCriteria, String label, String propertyName, FieldType type, Action... actions) {
        this.entryCriteria = entryCriteria;
        this.propertyName = propertyName;
        this.type = type;
        this.label = label;
        this.actions = List.of(actions);
    }

    @NotNull
    private static Function<org.ibs.cds.gode.codegenerator.model.deploy.LocalDeploymentRequirement, Function<CodeApp, String>> adminUrl() {
        return req -> codeApp -> "http://localhost:".concat(req.getValue()).concat("/").concat(CodeAppUtil.adminAppName(codeApp).toLowerCase());
    }

    @NotNull
    private static Predicate<CodeApp> requireJPA() {
        return c -> DeploymentRequirement.getStoreRequirements(c).containsKey(StoreType.JPA);
    }

    @NotNull
    private static Predicate<CodeApp> requireCassandra() {
        return c -> DeploymentRequirement.getStoreRequirements(c).containsKey(StoreType.CASSANDRA);
    }

    @NotNull
    private static Predicate<CodeApp> requireMongoDB() {
        return c -> DeploymentRequirement.getStoreRequirements(c).containsKey(StoreType.MONGODB);
    }

    @NotNull
    private static Predicate<CodeApp> requireQueueServer() {
        return c -> c.isSystemQueue() || DeploymentRequirement.isQueueManagerNeeded(DeploymentRequirement.getStoreRequirements(c));
    }

    @JsonIgnore
    public static LocalDeploymentRequirement from(String propertyName, String value, CodeApp app) {
        return Stream.of(LocalDeploymentRequirement.values())
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
                .stream(LocalDeploymentRequirement.values())
                .filter(k -> k.entryCriteria.test(app))
                .collect(Collectors.toMap(s -> s.propertyName, s -> s.getLabel()));
    }

    @Override
    public String toString() {
        return this.propertyName;
    }
}
