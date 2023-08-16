package example.micronaut;

import com.google.template.soy.SoyFileSet;
import com.google.template.soy.jbcsrc.api.SoySauce;
import com.google.template.soy.jbcsrc.api.SoySauceBuilder;
import io.micronaut.views.soy.SoyFileSetProvider;
import jakarta.inject.Singleton;

@Singleton
public class CompiledTemplatesProvider implements SoyFileSetProvider {

    @Override
    public SoyFileSet provideSoyFileSet() {
        return null;
    }

    @Override
    public SoySauce provideCompiledTemplates() {
        SoySauceBuilder builder = new SoySauceBuilder();
        return builder.build();
    }

}
