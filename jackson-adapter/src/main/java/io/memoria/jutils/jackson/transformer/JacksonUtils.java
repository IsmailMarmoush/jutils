package io.memoria.jutils.jackson.transformer;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactoryBuilder;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator.Feature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import io.memoria.jutils.core.value.Id;
import io.vavr.jackson.datatype.VavrModule;

import java.text.SimpleDateFormat;

public class JacksonUtils {
  public static ObjectMapper addJ8Modules(ObjectMapper om) {
    return om.registerModule(new ParameterNamesModule())
             .registerModule(new Jdk8Module())
             .registerModule(new JavaTimeModule());
  }

  public static ObjectMapper addVavrModule(ObjectMapper om) {
    return om.registerModule(new VavrModule());
  }

  public static ObjectMapper defaultJson(Class<?>... baseClass) {
    ObjectMapper om = JsonMapper.builder().build();
    om = setDateFormat(om);
    om = addJ8Modules(om);
    om = addVavrModule(om);
    om = jsonPrettyPrinting(om);
    om.registerModule(jutilsModule());
    om.configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false);
    return mixinWrapperObjectFormat(om, baseClass);
  }

  public static ObjectMapper defaultYaml(Class<?>... baseClass) {
    var yfb = new YAMLFactoryBuilder(YAMLFactory.builder().build());
    yfb.configure(Feature.INDENT_ARRAYS, true);
    ObjectMapper om = new ObjectMapper(yfb.build());
    om = setDateFormat(om);
    om = addJ8Modules(om);
    om = addVavrModule(om);
    om.registerModule(jutilsModule());
    om.configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false);
    return mixinWrapperObjectFormat(om, baseClass);
  }

  /**
   * Maps inheriting classes simple names written "As.WRAPPER_OBJECT" to this baseClass argument
   * <p>
   * note for this to work properly subclasses have to be in separate files from their baseClass otherwise Jvm will
   * return "BaseClass$ChildClass" kind of naming
   *
   * @param baseClass base classes
   * @return a new {@link JacksonUtils}
   */
  public static ObjectMapper mixinWrapperObjectFormat(ObjectMapper om, Class<?>... baseClass) {
    @JsonTypeInfo(include = As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
    class WrapperObjectByClassName {}
    for (Class<?> cls : baseClass) {
      om.addMixIn(cls, WrapperObjectByClassName.class);
    }
    return om;
  }

  public static ObjectMapper setDateFormat(ObjectMapper om) {
    return om.setDateFormat(new SimpleDateFormat("yyyy-MM-dd" + "'T'" + "HH:mm:ss"));
  }

  public static ObjectMapper jsonPrettyPrinting(ObjectMapper om) {
    var printer = new DefaultPrettyPrinter().withoutSpacesInObjectEntries();
    printer.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);
    var resultMapper = om.enable(SerializationFeature.INDENT_OUTPUT);
    resultMapper.setDefaultPrettyPrinter(printer);

    return resultMapper;
  }

  public static SimpleModule jutilsModule() {
    SimpleModule jutils = new SimpleModule();
    jutils.addDeserializer(Id.class, new IdDeserializer());
    jutils.addSerializer(Id.class, new IdSerializer());
    return jutils;
  }

  private JacksonUtils() {}
}