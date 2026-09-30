package moe.fruit.provider;

import java.util.*;
import moe.fruit.data.*;

public interface IndexProvider extends Provider<ClassItem>{
	 List<ClassItem> getHeader();
}
