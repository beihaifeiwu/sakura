package sakura.common.jackson.module;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.vividsolutions.jts.geom.Coordinate;
import sakura.common.lang.CLS;

import java.io.IOException;

/**
 * Created by liupin on 2017/8/7.
 */
public class JTSExtrasModule extends SimpleModule {

    @Override
    public String getModuleName() {
        return "jts-extras";
    }

    @Override
    public Version version() {
        return Version.unknownVersion();
    }

    @Override
    public void setupModule(SetupContext context) {
        if (CLS.isPresent("com.vividsolutions.jts.geom.Coordinate")) {
            addSerializer(Coordinate.class, new CoordinateSerializer());
            addDeserializer(Coordinate.class, new CoordinateDeserializer());
        }
        super.setupModule(context);
    }

    private static class CoordinateSerializer extends JsonSerializer<Coordinate> {

        @Override
        public void serialize(Coordinate value,
                              JsonGenerator gen,
                              SerializerProvider serializers) throws IOException {
            gen.writeStartObject();
            gen.writeNumberField("x", value.x);
            gen.writeNumberField("y", value.y);
            if (!Double.isNaN(value.z)) {
                gen.writeNumberField("z", value.z);
            }
            gen.writeEndObject();
        }

    }

    private static class CoordinateDeserializer extends JsonDeserializer<Coordinate> {

        @Override
        public Coordinate deserialize(JsonParser p,
                                      DeserializationContext ctx) throws IOException {
            Coordinate coordinate = new Coordinate();
            JsonToken t = p.getCurrentToken();
            if (t == JsonToken.START_OBJECT) {
                t = p.nextToken();
            }
            for (; t != JsonToken.END_OBJECT; t = p.nextToken()) {
                expect(ctx, JsonToken.FIELD_NAME, t);
                String fieldName = p.getCurrentName();
                p.nextToken();
                switch (fieldName.toLowerCase()) {
                    case "x":
                        coordinate.x = p.getDoubleValue();
                        break;
                    case "y":
                        coordinate.y = p.getDoubleValue();
                        break;
                    case "z":
                        coordinate.z = p.getDoubleValue();
                        break;
                    default:
                        break;
                }
            }
            return coordinate;
        }

        private void expect(DeserializationContext context,
                            JsonToken expected, JsonToken actual) throws JsonMappingException {
            if (actual != expected) {
                context.reportInputMismatch(this, String.format("Problem deserializing %s: expecting %s, found %s",
                        handledType().getName(), expected, actual));
            }
        }
    }

}
