public enum Series {
	TOS("StarTrek"),
	TNG("NextGen"),
	DS9("DS9"),
	VOY("Voyager"),
	ENT("Enterprise");
	private final String name;

	Series(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}