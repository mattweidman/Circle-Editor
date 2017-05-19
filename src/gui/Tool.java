package gui;

public enum Tool {
	
	PAN, MOVE, RESIZE, ADD, REMOVE;
	
	@Override
	public String toString() {
		switch(this) {
			case PAN: return "Pan";
			case MOVE: return "Move circle";
			case RESIZE: return "Scale circle";
			case ADD: return "Add circle";
			case REMOVE: return "Delete circle";
			default: return "";
		}
	}

}
