

package org.springframework.scripting.groovy;

import java.util.HashSet;
import java.util.Set;

import org.codehaus.groovy.control.BytecodeProcessor;


public class MyBytecodeProcessor implements BytecodeProcessor {

	public final Set<String> processed = new HashSet<String>();

	@Override
	public byte[] processBytecode(String name, byte[] original) {
		this.processed.add(name);
		return original;
	}

}
