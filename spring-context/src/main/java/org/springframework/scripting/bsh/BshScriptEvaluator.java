

package org.springframework.scripting.bsh;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

import bsh.EvalError;
import bsh.Interpreter;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.lang.Nullable;
import org.springframework.scripting.ScriptCompilationException;
import org.springframework.scripting.ScriptEvaluator;
import org.springframework.scripting.ScriptSource;

/**
 * BeanShell-based implementation of Spring's {@link ScriptEvaluator} strategy interface.
 *

 * @since 4.0
 * @see Interpreter#eval(String)
 */
public class BshScriptEvaluator implements ScriptEvaluator, BeanClassLoaderAware {

	@Nullable
	private ClassLoader classLoader;


	/**
	 * Construct a new BshScriptEvaluator.
	 */
	public BshScriptEvaluator() {
	}

	/**
	 * Construct a new BshScriptEvaluator.
	 * @param classLoader the ClassLoader to use for the {@link Interpreter}
	 */
	public BshScriptEvaluator(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}


	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}


	@Override
	@Nullable
	public Object evaluate(ScriptSource script) {
		return evaluate(script, null);
	}

	@Override
	@Nullable
	public Object evaluate(ScriptSource script, @Nullable Map<String, Object> arguments) {
		try {
			Interpreter interpreter = new Interpreter();
			interpreter.setClassLoader(this.classLoader);
			if (arguments != null) {
				for (Map.Entry<String, Object> entry : arguments.entrySet()) {
					interpreter.set(entry.getKey(), entry.getValue());
				}
			}
			return interpreter.eval(new StringReader(script.getScriptAsString()));
		}
		catch (IOException ex) {
			throw new ScriptCompilationException(script, "Cannot access BeanShell script", ex);
		}
		catch (EvalError ex) {
			throw new ScriptCompilationException(script, ex);
		}
	}

}
