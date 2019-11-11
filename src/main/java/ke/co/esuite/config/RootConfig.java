package ke.co.esuite.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@ComponentScan(basePackages = { "ke.co.esuite" }, excludeFilters = {
		@Filter(type = FilterType.ANNOTATION, value = EnableWebMvc.class) })
@PropertySource({
	"classpath:application.properties"})
public class RootConfig {
	
	
	/*@Bean
    public ObjectMapper jsonMapper() {
        return new ObjectMapper();
    }*/

}
