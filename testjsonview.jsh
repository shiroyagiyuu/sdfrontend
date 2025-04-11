import pureplus.*;
import java.io.File;

JSONViewer   viewer = new JSONViewer();
viewer.load(new File("out.json"));
viewer.init();
