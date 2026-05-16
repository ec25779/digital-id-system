package com.github.ec25779.digitalid.util.json;

import com.github.ec25779.digitalid.log.AuditAction;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.stream.Collectors;

public class AuditActionAdapter implements JsonSerializer<AuditAction>, JsonDeserializer<AuditAction> {

    private static final String TYPE_PROPERTY = "type";

    private static final Map<String, Class<? extends AuditAction>> ACTIONS = Map.of(
        "CreateIdentity", AuditAction.CreateIdentityAction.class,
        "UpdateIdentityFullName", AuditAction.UpdateIdentityFullNameAction.class,
        "UpdateIdentityAddress", AuditAction.UpdateIdentityAddressAction.class,
        "UpdateIdentityStatus", AuditAction.UpdateIdentityStatusAction.class,
        "LookupIdentity", AuditAction.LookupIdentityAction.class,
        "VerifyIdentity", AuditAction.VerifyIdentityAction.class,
        "VerifyIdentityBetween", AuditAction.VerifyIdentityBetweenAction.class
    );

    private static final Map<Class<? extends AuditAction>, String> ACTIONS_BY_CLASS = ACTIONS.entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

    @Override
    public JsonElement serialize(AuditAction input, Type type, JsonSerializationContext context) {
        String name = ACTIONS_BY_CLASS.get(input.getClass());
        if (name == null) {
            throw new JsonParseException("Unknown audit action: " + input.getClass().getName());
        }

        JsonObject out = context.serialize(input, input.getClass()).getAsJsonObject();
        out.addProperty(TYPE_PROPERTY, name);
        return out;
    }

    @Override
    public AuditAction deserialize(JsonElement input, Type type, JsonDeserializationContext context) {
        JsonObject json = input.getAsJsonObject();
        String name = json.get(TYPE_PROPERTY).getAsString();
        Class<? extends AuditAction> actionType = ACTIONS.get(name);

        if (actionType == null) {
            throw new JsonParseException("Unknown audit action: " + name);
        }

        return context.deserialize(json, actionType);
    }

}
