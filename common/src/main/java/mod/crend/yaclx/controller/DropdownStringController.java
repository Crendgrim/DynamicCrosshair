package mod.crend.yaclx.controller;

import dev.isxander.yacl.api.Controller;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.controller.StringControllerBuilder;
import dev.isxander.yacl.api.utils.Dimension;
import dev.isxander.yacl.gui.AbstractWidget;
import dev.isxander.yacl.gui.YACLScreen;
import dev.isxander.yacl.impl.controller.StringControllerBuilderImpl;

import java.util.Arrays;
import java.util.List;

public class DropdownStringController extends AbstractDropdownController<String> {

	public DropdownStringController(Option<String> option, List<String> allowedValues) {
		super(option, allowedValues);
	}

	@Override
	public String getString() {
		return option().pendingValue().toString();
	}

	@Override
	public void setFromString(String value) {
		option().requestSet(getValidValue(value));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
		return new DropdownStringControllerElement(this, screen, widgetDimension);
	}


	public interface DropdownControllerBuilder<T> extends StringControllerBuilder {
		DropdownStringController.DropdownStringControllerBuilderImpl allowedValues(List<String> allowedValues);
		DropdownStringController.DropdownStringControllerBuilderImpl allowedValues(String[] allowedValues);

		static DropdownStringController.DropdownStringControllerBuilderImpl create(Option<String> option) {
			return new DropdownStringController.DropdownStringControllerBuilderImpl(option);
		}
	}

	public static class DropdownStringControllerBuilderImpl extends StringControllerBuilderImpl implements DropdownControllerBuilder<String> {
		private List<String> allowedValues;

		public DropdownStringControllerBuilderImpl(Option<String> option) {
			super(option);
		}

		@Override
		public DropdownStringControllerBuilderImpl allowedValues(List<String> allowedValues) {
			this.allowedValues = allowedValues;
			return this;
		}

		@Override
		public DropdownStringControllerBuilderImpl allowedValues(String[] options) {
			return allowedValues(Arrays.asList(options));
		}

		@Override
		public Controller<String> build() {
			return new DropdownStringController(option, allowedValues);
		}

	}

	public static class DropdownStringControllerElement extends DropdownControllerElement<String, String> {
		DropdownStringController controller;

		public DropdownStringControllerElement(DropdownStringController control, YACLScreen screen, Dimension<Integer> dim) {
			super(control, screen, dim);
			this.controller = control;
		}

		@Override
		public List<String> getMatchingValues() {
			return controller.getAllowedValues().stream()
					.filter(this::matchingValue)
					.sorted()
					.toList();
		}

		public String getString(String object) {
			return object;
		}
	}

}
