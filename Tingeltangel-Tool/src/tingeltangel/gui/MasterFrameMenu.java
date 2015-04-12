
package tingeltangel.gui;

import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;


public class MasterFrameMenu implements ActionListener {
    
    private final static LinkedList<String> keys = new LinkedList<String>();
    private final static HashMap<String, String> values = new HashMap<String, String>();
    private final static HashMap<String, MenuItem> items = new HashMap<String, MenuItem>();
    private static MenuCallback callback = null;
    private String id;
    
    private MasterFrameMenu(String id) {
        this.id = id;
    }
    
    static {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(MasterFrameMenu.class.getResourceAsStream("/menu.properties")));
            String row;
            while((row = in.readLine()) != null) {
                row = row.trim();
                if((!row.isEmpty()) && (!row.startsWith("#"))) {
                    int p = row.indexOf("=");
                    String key = row.substring(0, p).trim();
                    String value = row.substring(p + 1).trim();
                    keys.add(key);
                    values.put(key, value);
                }
            }
        } catch(Exception e) {
            throw new Error("unable to load menu from 'menu.properties'");
        }
    }
    
    public static void setEnabled(String id, boolean enabled) {
        items.get(id).setEnabled(enabled);
    }
    
    public static void setMenuCallback(MenuCallback menuCallback) {
        callback = menuCallback;
    }
    
    private static MenuItem generateMenuItem(TreeElement element) {
        if(element.isLeaf()) {
            MenuItem item = new MenuItem(element.getCaption());
            item.addActionListener(new MasterFrameMenu(element.getFullID()));
            if(element.isHidden()) {
                item.setEnabled(false);
            }
            items.put(element.getFullID(), item);
            return(item);
        } else {
            Menu menu = new Menu(element.getCaption());
            Iterator<TreeElement> i = element.getChilds();
            while(i.hasNext()) {
                menu.add(generateMenuItem(i.next()));
            }
            if(element.isHidden()) {
                menu.setEnabled(false);
            }
            items.put(element.getFullID(), menu);
            return(menu);
        }
    }

    
    public static MenuBar getMenuBar() {
        TreeElement root = new TreeElement(null);
        Iterator<String> keyIterator = keys.iterator();
        while(keyIterator.hasNext()) {
            String rawKey = keyIterator.next().trim();
            TreeElement element = root.get(rawKey.split("\\."));
            element.setCaption(values.get(rawKey));
        }
        
        MenuBar menuBar = new MenuBar();
        Iterator<TreeElement> i = root.getChilds();
        while(i.hasNext()) {
            TreeElement element = i.next();
            menuBar.add((Menu)generateMenuItem(element));
        }
        
        return(menuBar);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(callback == null) {
            throw new Error();
        }
        callback.callback(id);
    }
}
class TreeElement {
    
    private String caption = null;
    private String id;
    private LinkedList<TreeElement> childs = new LinkedList<TreeElement>();
    private TreeElement parent = null;
    
    public TreeElement(String id) {
        this.id = id;
    }
    
    public void addChild(TreeElement element) {
        childs.add(element);
        element.parent = this;
    }
    
    public Iterator<TreeElement> getChilds() {
        return(childs.iterator());
    }
    
    private TreeElement get(String childID) {
        Iterator<TreeElement> childIterator = childs.iterator();
        while(childIterator.hasNext()) {
            TreeElement child = childIterator.next();
            if(child.getID().equals(childID)) {
                return(child);
            }
        }
        TreeElement newChild = new TreeElement(childID);
        addChild(newChild);
        return(newChild);
    }
    
    public TreeElement get(String[] path) {
        TreeElement current = this;
        for(int i = 0; i < path.length; i++) {
            current = current.get(path[i]);
        }
        return(current);
    }
    
    public TreeElement getParent() {
        return(parent);
    }
    
    public boolean isHidden() {
        return(caption.startsWith("#"));
    }
    
    public String getCaption() {
        if(isHidden()) {
            return(caption.substring(1));
        }
        return(caption);
    }
    
    public void setCaption(String caption) {
        this.caption = caption;
    }
    
    public String getID() {
        return(id);
    }
    
    public String getFullID() {
        String fullID = "";
        TreeElement current = this;
        while(current != null) {
            if(current.getID() != null) {
                if(fullID.isEmpty()) {
                    fullID = current.getID();
                } else {
                    fullID = current.getID() + "." + fullID;
                }
            }
            current = current.getParent();
        }
        return(fullID);
    }
    
    public boolean isLeaf() {
        return(childs.isEmpty());
    }
}