package com.alorma.github.injector.component;

import com.alorma.github.GitskariosApplication;
import com.alorma.github.injector.NotificationsScope;
import com.alorma.github.injector.module.NotificationsModule;
import com.alorma.github.notifications.AppJobManager;
import com.alorma.github.notifications.AppNotificationsManager;
import com.alorma.github.ui.fragment.NotificationsFragment;
import dagger.Component;
import javax.inject.Named;

@NotificationsScope @Component(modules = NotificationsModule.class) public interface NotificationsComponent {

  @Named("NotificationsJobManager")
  AppJobManager getJobManager();

  AppNotificationsManager getNotificationsManager();

  void inject(GitskariosApplication application);
}
