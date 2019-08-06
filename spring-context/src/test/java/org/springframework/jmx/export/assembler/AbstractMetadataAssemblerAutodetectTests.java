

package org.springframework.jmx.export.assembler;

import org.springframework.jmx.export.metadata.JmxAttributeSource;


public abstract class AbstractMetadataAssemblerAutodetectTests extends AbstractAutodetectTests {

	@Override
	protected AutodetectCapableMBeanInfoAssembler getAssembler() {
		MetadataMBeanInfoAssembler assembler = new MetadataMBeanInfoAssembler();
		assembler.setAttributeSource(getAttributeSource());
		return assembler;
	}

	protected abstract JmxAttributeSource getAttributeSource();

}
