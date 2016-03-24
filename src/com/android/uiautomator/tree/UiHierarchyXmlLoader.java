/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.uiautomator.tree;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.eclipse.swt.graphics.Rectangle;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.newland.common.FinalConfig;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class UiHierarchyXmlLoader {

    private BasicTreeNode mRootNode;
    private List<Rectangle> mNafNodes;
    private List<BasicTreeNode> mNodeList;
    public UiHierarchyXmlLoader() {
    }
    
    public Document getDocument(String filePath){
    	Document document = null;
    	File file = new File(filePath);
        if (file.exists()) {
            SAXReader saxReader = new SAXReader();
            try {
                document = saxReader.read(file);
            } catch (DocumentException e) {    
                System.out.println("文件加载异常：" + filePath);       
                e.printStackTrace();
            }
        } else{
            System.out.println("文件不存在 : " + filePath);
        }  
        return document;
    }
    
    @SuppressWarnings("unchecked")
    public static List<Element> getElementObjects(Document document,String elementPath) {
        return document.selectNodes(elementPath);
    }

    /**
     * Uses a SAX parser to process XML dump
     * @param xmlPath
     * @return
     */
    public BasicTreeNode parseXml(String xmlPath) {
        mRootNode = null;
        mNafNodes = new ArrayList<Rectangle>();
        mNodeList = new ArrayList<BasicTreeNode>();
        // standard boilerplate to get a SAX parser
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = null;
        try {
            parser = factory.newSAXParser();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return null;
        } catch (SAXException e) {
            e.printStackTrace();
            return null;
        }
        // handler class for SAX parser to receiver standard parsing events:
        // e.g. on reading "<foo>", startElement is called, on reading "</foo>",
        // endElement is called
        DefaultHandler handler = new DefaultHandler(){
            BasicTreeNode mParentNode;
            BasicTreeNode mWorkingNode;
            @Override
            public void startElement(String uri, String localName, String qName,
                    Attributes attributes) throws SAXException {
                boolean nodeCreated = false;
                // starting an element implies that the element that has not yet been closed
                // will be the parent of the element that is being started here
                mParentNode = mWorkingNode;
                if ("hierarchy".equals(qName)) {
                    int rotation = 0;
                    for (int i = 0; i < attributes.getLength(); i++) {
                        if ("rotation".equals(attributes.getQName(i))) {
                            try {
                                rotation = Integer.parseInt(attributes.getValue(i));
                            } catch (NumberFormatException nfe) {
                                // do nothing
                            }
                        }
                    }
                    mWorkingNode = new RootWindowNode(attributes.getValue("windowName"), rotation);
                    nodeCreated = true;
                } else if ("node".equals(qName)) {
                    UiNode tmpNode = new UiNode();
                    for (int i = 0; i < attributes.getLength(); i++) {
                        tmpNode.addAtrribute(attributes.getQName(i), attributes.getValue(i));
                    }
                    tmpNode.addAtrribute("xpath",tmpNode.getXpath());
                    mWorkingNode = tmpNode;
                    nodeCreated = true;
                    // check if current node is NAF
                    String naf = tmpNode.getAttribute("NAF");
                    if ("true".equals(naf)) {
                        mNafNodes.add(new Rectangle(tmpNode.x, tmpNode.y,
                                tmpNode.width, tmpNode.height));
                    }
                }
                // nodeCreated will be false if the element started is neither
                // "hierarchy" nor "node"
                if (nodeCreated) {
                    if (mRootNode == null) {
                        // this will only happen once
                        mRootNode = mWorkingNode;
                    }
                    if (mParentNode != null) {
                        mParentNode.addChild(mWorkingNode);
                        /*if(mWorkingNode.getParent()!=null){
                        	String pXpath;
                        	try{
                        		pXpath = ((UiNode)mWorkingNode.getParent()).getAttribute("xpath");
                        		pXpath = pXpath.substring(1);
                        		((UiNode)mWorkingNode).addAtrribute("xpath","/" + pXpath + ((UiNode)mWorkingNode).getXpath());
                        	}catch(Exception e){
                        		((UiNode)mWorkingNode).addAtrribute("xpath","/" + ((UiNode)mWorkingNode).getXpath());
                        	}
                        }*/
                        if(mWorkingNode.getParent()!=null){
                        	String pXpath;
                        	String fXpath;
                        	String xpath;
                        	String xpath2 = null;
                        	String uiaStr = null;
                        	BasicTreeNode  parent = null;
                        	int beginIndex = 0;
                        	UiNode myNode = null;
                        	boolean isP = false;
                        	try{
                        		myNode = (UiNode)mWorkingNode;
                        		parent = mWorkingNode.getParent();
                        		pXpath = ((UiNode)parent).getAttribute("fXpath").substring(1);
                        		fXpath = "/" + pXpath + ((UiNode)mWorkingNode).getXpath();
                        		xpath = "/" + ((UiNode)mWorkingNode).getXpath();
                        		

                        		String pXpath2 = ((UiNode)parent).getAttribute("xpath2").substring(1);
                        		xpath2 = "/" + pXpath2 + ((UiNode)mWorkingNode).getXpath2();
                        		uiaStr = getUiSelector(myNode);
//                        		System.out.println(xpath);
//                        		System.out.println("-------------"+xpath.replaceAll("\\\\\"", "\""));
                        		List<Element> list = UiHierarchyXmlLoader.getElementObjects(FinalConfig.document, xpath.replaceAll("\\\\\"", "\""));
                        		while(list.size()>1){
//                        			System.out.println("重复:"+xpath);
                        			try{
                            			parent = parent.getParent();
                            			myNode = (UiNode)parent;
                            			uiaStr = getUiSelector(myNode) + ".childSelector("+uiaStr+")";
                            			beginIndex = ((UiNode)parent).getAttribute("fXpath").length();
                            			xpath = "/" + fXpath.substring(beginIndex);
                            			list = UiHierarchyXmlLoader.getElementObjects(FinalConfig.document, xpath.replaceAll("\\\\\"", "\""));
                        			}catch(Exception e1){
//                        				e1.printStackTrace();
                        				isP = true;
                        				xpath = fXpath;
                                		((UiNode)mWorkingNode).addAtrribute("fXpath",fXpath);
                                		((UiNode)mWorkingNode).addAtrribute("xpath",fXpath);
                                		((UiNode)mWorkingNode).addAtrribute("uiaSelector",uiaStr);
                        				break;
                        			}
                        		}
                        		if(!isP){
                            		((UiNode)mWorkingNode).addAtrribute("fXpath",fXpath);
                            		((UiNode)mWorkingNode).addAtrribute("xpath",xpath);
                            		((UiNode)mWorkingNode).addAtrribute("uiaSelector",uiaStr);
                        		}
                        	}catch(Exception e){
//                        		e.printStackTrace();
                        		fXpath = "/" + ((UiNode)mWorkingNode).getXpath();
                        		xpath = fXpath;
                        		xpath2 = "/" + ((UiNode)mWorkingNode).getXpath();
                        		((UiNode)mWorkingNode).addAtrribute("fXpath",fXpath);
                        		((UiNode)mWorkingNode).addAtrribute("xpath",xpath);
                        		((UiNode)mWorkingNode).addAtrribute("uiaSelector",uiaStr);
                        	}
                    		((UiNode)mWorkingNode).addAtrribute("xpath2",xpath2);
                        	if(xpath.equals(fXpath)){
                        		((UiNode)mWorkingNode).addAtrribute("isOnly","false");
                        		//===================================
                        		((UiNode)mWorkingNode).addAtrribute("uiaSelector","");
                        		//===================================
                        	}else{
                        		((UiNode)mWorkingNode).addAtrribute("isOnly","true");
                        	}
                            isP = false;
//                        	System.out.println(uiaStr);
                        }
                        mNodeList.add(mWorkingNode);
                    }
                }
            }

            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException {
                //mParentNode should never be null here in a well formed XML
                if (mParentNode != null) {
                    // closing an element implies that we are back to working on
                    // the parent node of the element just closed, i.e. continue to
                    // parse more child nodes
                    mWorkingNode = mParentNode;
                    mParentNode = mParentNode.getParent();
                }
            }
        };
        try {
            parser.parse(new File(xmlPath), handler);
        } catch (SAXException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return mRootNode;
    }
    
    public static String getUiSelector(UiNode node){
    	String uiSelector = null;
    	uiSelector = "new UiSelector().className(\\\""+ node.getAttribute("class") + "\\\")";
    	String text = node.getAttribute("text");
    	if(text!=null && !text.equals("")){
			text = text.replaceAll("'", "\\\\'");
			text = text.replaceAll("\"", "\\\\\"");
    		uiSelector = uiSelector + ".textContains(\\\""+ text + "\\\")";
    	}
    	if(node.getAttribute("index")!=null && !node.getAttribute("index").equals("")){
//    		uiSelector = uiSelector + ".index("+ node.getAttribute("index") + ")";
    	}
    	if(node.getAttribute("resource-id")!=null && !node.getAttribute("resource-id").equals("")){
    		uiSelector = uiSelector + ".resourceId(\\\""+ node.getAttribute("resource-id") + "\\\")";
    	}
				
				
    	return uiSelector;
    }

    /**
     * Returns the list of "Not Accessibility Friendly" nodes found during parsing.
     *
     * Call this function after parsing
     *
     * @return
     */
    public List<Rectangle> getNafNodes() {
        return Collections.unmodifiableList(mNafNodes);
    }

    public List<BasicTreeNode> getAllNodes(){
        return mNodeList;
    }
}
