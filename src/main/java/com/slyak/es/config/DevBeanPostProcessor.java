package com.slyak.es.config;

import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.List;

/**
 * @author stormning
 * @since 1.0
 */
@Component
@Slf4j
public class DevBeanPostProcessor implements BeanPostProcessor, ApplicationListener<ApplicationReadyEvent>, ApplicationContextAware {

	private List<String> watchDirectory = Lists.newArrayList();

	private ApplicationContext applicationContext;

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		String codeLocation = getSourceCodeLocation();
		if (codeLocation != null) {
			if (bean instanceof ThymeleafProperties) {
				String templateDir = codeLocation + "/src/main/resources/templates/";
				ThymeleafProperties props = (ThymeleafProperties) bean;
				props.setPrefix(templateDir);
				watchIt(templateDir);
			} else if (bean instanceof WebProperties) {
				WebProperties props = (WebProperties) bean;
				WebProperties.Resources resources = props.getResources();
				String[] exist = resources.getStaticLocations();
				List<String> devLocs = Lists.newArrayList();
				for (String loc : exist) {
					if (loc.startsWith(ResourceUtils.CLASSPATH_URL_PREFIX)) {
						String dev_loc
								= loc.replace(ResourceUtils.CLASSPATH_URL_PREFIX, codeLocation + "/src/main/resources");
						devLocs.add(dev_loc);
						watchIt(dev_loc);
					} else {
						devLocs.add(loc);
					}
				}
				resources.setStaticLocations(devLocs.toArray(new String[0]));
				resources.getCache().setPeriod(Duration.ZERO);
			}
		}
		return bean;
	}

	private void watchIt(String dir) {
		watchDirectory.add(dir.startsWith("file:") ? dir.replace("file:", "") : dir);
	}

	private String getSourceCodeLocation() {
		String classesPath = getClass().getProtectionDomain().getCodeSource().getLocation().toString();
		int index = classesPath.indexOf("/target/classes");
		if (index > 0) {
			return classesPath.substring(0, index);
		}
		return null;
	}

	@Override
	@SneakyThrows
	public void onApplicationEvent(ApplicationReadyEvent event) {
		String liveReloadServerClass = "org.springframework.boot.devtools.autoconfigure.OptionalLiveReloadServer";
		boolean present = ClassUtils.isPresent(liveReloadServerClass, getClass().getClassLoader());
		if (present) {
			Object liveReloadServer = applicationContext.getBean(Class.forName(liveReloadServerClass));
			if (watchDirectory.size() > 0) {
				Method reloadMethod = ReflectionUtils.findMethod(liveReloadServer.getClass(), "triggerReload");
				FileAlterationMonitor monitor = new FileAlterationMonitor(600);
				for (String dir : watchDirectory) {
					FileAlterationObserver observer = new FileAlterationObserver(dir);
					observer.addListener(new FileAlterationListenerAdaptor() {
						@Override
						public void onFileCreate(File file) {
							log.info("onFileCreate {}", file);
							ReflectionUtils.invokeMethod(reloadMethod, liveReloadServer);
						}

						@Override
						public void onFileChange(File file) {
							log.info("onFileChange {}", file);
							ReflectionUtils.invokeMethod(reloadMethod, liveReloadServer);
						}

						@Override
						public void onFileDelete(File file) {
							log.info("onFileDelete {}", file);
							ReflectionUtils.invokeMethod(reloadMethod, liveReloadServer);
						}
					});
					monitor.addObserver(observer);
				}
				monitor.start();
			}
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}