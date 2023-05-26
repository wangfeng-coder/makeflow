/* Licensed under the Apache License, Version 2.0 (the "License");
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
package com.makeid.makeflow.template.bpmn.conventer.parser;


import com.makeid.makeflow.template.bpmn.constants.BpmnXMLConstants;
import com.makeid.makeflow.template.bpmn.model.BaseElement;
import com.makeid.makeflow.template.bpmn.model.BpmnModel;
import com.makeid.makeflow.template.bpmn.model.ExtensionAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLStreamReader;
import java.util.LinkedList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

/**

 */
public abstract class BaseChildElementParser implements BpmnXMLConstants {

  protected static final Logger LOGGER = LoggerFactory.getLogger(BaseChildElementParser.class);

  public abstract String getElementName();

  public abstract void parseChildElement(XMLStreamReader xtr, BaseElement parentElement, BpmnModel model) throws Exception;

  protected void parseChildElements(XMLStreamReader xtr, BaseElement parentElement, BpmnModel model, BaseChildElementParser parser) throws Exception {
    boolean readyWithChildElements = false;
    while (!readyWithChildElements  && xtr.hasNext()) {
      xtr.next();
      if (xtr.isStartElement()) {
        if (parser.getElementName().equals(xtr.getLocalName())) {
          parser.parseChildElement(xtr, parentElement, model);
        }

      } else if (xtr.isEndElement() && getElementName().equalsIgnoreCase(xtr.getLocalName())) {
        readyWithChildElements = true;
      }
    }
  }

  public boolean accepts(BaseElement element) {
    return element != null;
  }

  protected List<ExtensionAttribute> parseExtensionAttributes(XMLStreamReader xtr,
                                                              BaseElement parentElement,
                                                              BpmnModel model) {
    List<ExtensionAttribute> attributes = new LinkedList<>();

    for(int i=0; i < xtr.getAttributeCount(); i++) {
      if(ACTIVITI_EXTENSIONS_NAMESPACE.equals(xtr.getAttributeNamespace(i))) {
        ExtensionAttribute attr = new ExtensionAttribute(ACTIVITI_EXTENSIONS_NAMESPACE,
                                                         xtr.getAttributeLocalName(i));
        attr.setValue(xtr.getAttributeValue(i));
        attributes.add(attr);
      }
    }

    return unmodifiableList(attributes);
  }
}
