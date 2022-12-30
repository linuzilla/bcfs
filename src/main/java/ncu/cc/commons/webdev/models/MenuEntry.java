package ncu.cc.commons.webdev.models;

import ncu.cc.commons.utils.StackTraceUtil;
import ncu.cc.commons.webdev.annotations.MenuItem;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

public class MenuEntry {
    private final String name;
    private final String urlpath;
    private final List<MenuEntry> submenu;
    private int order;
    private String authorities[];
    private String negativeAuthorities[];

    public MenuEntry(String name, String urlpath) {
        this.name = name;
        this.urlpath = urlpath;
        this.submenu = new ArrayList<>();
        this.order = 0;
    }

    public MenuEntry findByName(String name) {
        for (MenuEntry menuEntry: submenu) {
            if (menuEntry.name.equals(name)) {
                return menuEntry;
            }
        }
        return null;
    }

    public void addEntry(MenuItem menuItem, String path[], String urlpath, int from) {
        if (from < path.length) {
//            StackTraceUtil.print1(from);
//            StackTraceUtil.print1(path);
            MenuEntry menuEntry = findByName(path[from]);

            if (menuEntry == null) {
                menuEntry = new MenuEntry(path[from], urlpath);
                submenu.add(menuEntry);
            }

            if (from == path.length - 1) {
                menuEntry.order = menuItem.order();
                menuEntry.authorities = menuItem.authorities();
                menuEntry.negativeAuthorities = menuItem.negativeAuthorities();
                return;
            }

            Assert.isTrue(from < path.length, "Failed to add path " + String.join("/", path));
            menuEntry.addEntry(menuItem, path, urlpath, from + 1);
        } else {
            return;
        }
    }

    private MenuEntry findPrefixMatch(List<String> routePaths, int from, List<MenuEntry> store) {
        if (from < routePaths.size()) {
            for (MenuEntry menuEntry: this.submenu) {
                if (routePaths.get(from).equals(menuEntry.name)) {
                    store.add(menuEntry);
                    return menuEntry.findPrefixMatch(routePaths, from + 1, store);
                }
            }
        }
        return this;
    }

    public List<MenuEntry> findLongestPrefixMatch(List<String> routePaths) {
        List<MenuEntry> list = new ArrayList<>();
        findPrefixMatch(routePaths, 0, list);
        return list;
    }

    public String getName() {
        return name;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String[] getAuthorities() {
        return authorities;
    }

    public void setAuthorities(String[] authorities) {
        this.authorities = authorities;
    }

    public List<MenuEntry> getSubmenu() {
        return submenu;
    }

    public String getUrlpath() {
        return urlpath;
    }

    public String[] getNegativeAuthorities() {
        return negativeAuthorities;
    }

    public void setNegativeAuthorities(String[] negativeAuthorities) {
        this.negativeAuthorities = negativeAuthorities;
    }
}
