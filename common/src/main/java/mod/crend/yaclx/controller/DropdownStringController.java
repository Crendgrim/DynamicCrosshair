package mod.crend.yaclx.controller;

import dev.isxander.yacl.api.Option;

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

}
