/*
    Copyright (C) 2015   Martin Dames <martin@bastionbytes.de>
  
    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along
    with this program; if not, write to the Free Software Foundation, Inc.,
    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
  
*/

package tingeltangel.gui;

import tingeltangel.tools.Callback;
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
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import tingeltangel.core.Tupel;


public class MasterFrameMenu implements ActionListener {
    
    private final static LinkedList<String> keys = new LinkedList<String>();
    private final static HashMap<String, Tupel<String, Boolean>> values = new HashMap<String, Tupel<String, Boolean>>();
    private final static HashMap<String, JMenuItem> items = new HashMap<String, JMenuItem>();
    private static Callback<String> callback = null;
    private String id;
    
    private MasterFrameMenu(String id) {
        this.id = id;
    }
    
    static {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(MasterFrameMenu.class.getResourceAsStream("/menu.properties"), "UTF8"));
            String row;
            while((row = in.readLine()) != null) {
                row = row.trim();
                if((!row.isEmpty()) && (!row.startsWith("#"))) {
                    int p = row.indexOf("=");
                    String key = row.substring(0, p).trim();
                    String value = row.substring(p + 1).trim();
                    
                    boolean enabled = true;
                    if(key.startsWith("*")) {
                        enabled = false;
                        key = key.substring(1);
                    }
                    
                    keys.add(key);
                    values.put(key, new Tupel(value, enabled));
                }
            }
        } catch(Exception e) {
            throw new Error("unable to load menu from 'menu.properties'");
        }
    }
    
    public static void setEnabled(String id, boolean enabled) {
        items.get(id).setEnabled(enabled);
    }
    
    public static void setMenuCallback(Callback<String> menuCallback) {
        callback = menuCallback;
    }
    
    private static JMenuItem generateMenuItem(TreeElement element) {
        if(element.isLeaf()) {
            JMenuItem item = new JMenuItem(element.getCaption());
            
            item.getAccessibleContext().setAccessibleDescription(item.getLabel());
            
            item.addActionListener(new MasterFrameMenu(element.getFullID()));
            if(element.isHidden()) {
                item.setEnabled(false);
            } else if(!element.getEnabled()) {
                item.setEnabled(false);
            }
            items.put(element.getFullID(), item);
            return(item);
        } else {
            JMenu menu = new JMenu(element.getCaption());
            
            menu.getAccessibleContext().setAccessibleDescription(menu.getLabel());
            
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

    
    public static JMenuBar getMenuBar() {
        TreeElement root = new TreeElement(null);
        Iterator<String> keyIterator = keys.iterator();
        while(keyIterator.hasNext()) {
            String rawKey = keyIterator.next().trim();
            TreeElement element = root.get(rawKey.split("\\."));
            Tupel<String, Boolean> t = values.get(rawKey);
            element.setCaption(t.a);
            element.setEnabled(t.b);
        }
        
        JMenuBar menuBar = new JMenuBar();
        Iterator<TreeElement> i = root.getChilds();
        while(i.hasNext()) {
            TreeElement element = i.next();
            menuBar.add((JMenu)generateMenuItem(element));
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
    private boolean enabled = false;
    
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
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public boolean getEnabled() {
        return(enabled);
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