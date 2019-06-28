

package org.springframework.ui.context.support;

import org.springframework.lang.Nullable;
import org.springframework.ui.context.HierarchicalThemeSource;
import org.springframework.ui.context.Theme;
import org.springframework.ui.context.ThemeSource;

/**
 * Empty ThemeSource that delegates all calls to the parent ThemeSource.
 * If no parent is available, it simply won't resolve any theme.
 *
 * <p>Used as placeholder by UiApplicationContextUtils, if a context doesn't
 * define its own ThemeSource. Not intended for direct use in applications.
 *
 * @author Juergen Hoeller
 * @since 1.2.4
 * @see UiApplicationContextUtils
 */
public class DelegatingThemeSource implements HierarchicalThemeSource {

	@Nullable
	private ThemeSource parentThemeSource;


	@Override
	public void setParentThemeSource(@Nullable ThemeSource parentThemeSource) {
		this.parentThemeSource = parentThemeSource;
	}

	@Override
	@Nullable
	public ThemeSource getParentThemeSource() {
		return this.parentThemeSource;
	}


	@Override
	@Nullable
	public Theme getTheme(String themeName) {
		if (this.parentThemeSource != null) {
			return this.parentThemeSource.getTheme(themeName);
		}
		else {
			return null;
		}
	}

}
