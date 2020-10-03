/**
 * Licensed to MKS Group under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * MKS Group licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package m.k.s.sakai.app.question.tool.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Thach N. Le
 *
 */
public class TreeNode {

    /** Id variable. */
    private Long id;
    /** User parent. */
    
    private Long parent;
    
    /** Declare text variable. */
    private String text;
    /** Declare type variable. */
    private String type;
    /** Unique code . */
    private Map<String, String> a_attr = new HashMap<String, String>();
    
    private Map<String, Boolean> state = new HashMap<String, Boolean>();

    /** Declare children: true or List<TreeNode> */
    private Object children;

    /**
     * Get ID.
     * 
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * Set ID.
     * 
     * @param id
     *            set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get parent.
     * 
     * @return parent
     */
    public Long getParent() {
        return parent;
    }

    /**
     * Set parent.
     * 
     * @param parent
     *            set
     */
    public void setParent(Long parent) {
        this.parent = parent;
    }

    /**
     * Get Text.
     * 
     * @return text
     */
    public String getText() {
        return text;
    }

    /**
     * Set Test.
     * 
     * @param text
     *            set
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * get Children.
     * 
     * @return children
     */
    public Object getChildren() {
        return children;
    }

    /**
     * Set children.
     * 
     * @param children
     *            set
     */
    public void setChildren(List<TreeNode> children) {
        this.children = children;
    }

    public void setHasChildren() {
        this.children = true;
    }
    
    /**
     * Get Type.
     * 
     * @return type
     */
    public String getType() {
        return type;
    }

    /**
     * Set Type.
     * 
     * @param type
     *            set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Get value of a_attr.
     * 
     * @return the a_attr
     */
    public Map<String, String> getA_attr() {
        return a_attr;
    }

    /**
     * Set the value for a_attr.
     * 
     * @param a_attr
     *            the a_attr to set
     */
    public void setA_attr(Map<String, String> a_attr) {
        this.a_attr = a_attr;
    }

    /**
     * Add attr.
     * 
     * @param key
     *            set key of hash map.
     * @param value
     *            set value of hash map.
     */
    public void addAttr(String key, String value) {
        this.a_attr.put(key, value);
    }

    /**
    * Get value of state.
    * @return the state
    */
    public Map<String, Boolean> getState() {
        return state;
    }

    /**
     * Set the value for state.
     * @param state the state to set
     */
    public void setState(Map<String, Boolean> state) {
        this.state = state;
    }
    
    public void setOpened(Boolean opened) {
        state.put("opened", opened);
    }
    
    public void setDisabled(Boolean disabled) {
        state.put("disabled", disabled);
    }
    
    public void setSelected(Boolean selected) {
        state.put("selected", selected);
    }
}

