package com.whaleal.icefrog.http.webservice;

import com.whaleal.icefrog.core.collection.CollUtil;
import com.whaleal.icefrog.core.io.IoUtil;
import com.whaleal.icefrog.core.map.MapUtil;
import com.whaleal.icefrog.core.util.ObjectUtil;
import com.whaleal.icefrog.core.util.StrUtil;
import com.whaleal.icefrog.core.util.XmlUtil;
import com.whaleal.icefrog.http.HttpBase;
import com.whaleal.icefrog.http.HttpGlobalConfig;
import com.whaleal.icefrog.http.HttpRequest;
import com.whaleal.icefrog.http.HttpResponse;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * SOAP客户端
 *
 * <p>
 * 此对象用于构建一个SOAP消息，并通过HTTP接口发出消息内容。
 * SOAP消息本质上是一个XML文本，可以通过调用{@link #getMsgStr(boolean)} 方法获取消息体
 * <p>
 * 使用方法：
 *
 * <pre>
 * SoapClient client = SoapClient.create(url)
 * .setMethod(methodName, namespaceURI)
 * .setCharset(CharsetUtil.CHARSET_GBK)
 * .setParam(param1, "XXX");
 *
 * String response = client.send(true);
 *
 * </pre>
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class SoapClient extends HttpBase<SoapClient> {

	/**
	 * XML消息体的Content-Type
	 * soap1.1 : text/xml
	 * soap1.2 : application/soap+xml
	 * soap1.1与soap1.2区别:  https://www.cnblogs.com/qlqwjy/p/7577147.html
	 */
	private static final String CONTENT_TYPE_SOAP11_TEXT_XML = "text/xml;charset=";
	private static final String CONTENT_TYPE_SOAP12_SOAP_XML = "application/soap+xml;charset=";

	/**
	 * 请求的URL地址
	 */
	private String url;

	/**
	 * 默认连接超时
	 */
	private int connectionTimeout = HttpGlobalConfig.getTimeout();
	/**
	 * 默认读取超时
	 */
	private int readTimeout = HttpGlobalConfig.getTimeout();

	/**
	 * 消息工厂，用于创建消息
	 */
	private MessageFactory factory;
	/**
	 * SOAP消息
	 */
	private SOAPMessage message;
	/**
	 * 消息方法节点
	 */
	private SOAPBodyElement methodEle;
	/**
	 * 应用于方法上的命名空间URI
	 */
	private final String namespaceURI;
	/**
	 * Soap协议
	 * soap1.1 : text/xml
	 * soap1.2 : application/soap+xml
	 */
	private final SoapProtocol protocol;

	/**
	 * 创建SOAP客户端，默认使用soap1.1版本协议
	 *
	 * @param url WS的URL地址
	 * @return this
	 */
	public static SoapClient create(String url) {
		return new SoapClient(url);
	}

	/**
	 * 创建SOAP客户端
	 *
	 * @param url      WS的URL地址
	 * @param protocol 协议，见{@link SoapProtocol}
	 * @return this
	 */
	public static SoapClient create(String url, SoapProtocol protocol) {
		return new SoapClient(url, protocol);
	}

	/**
	 * 创建SOAP客户端
	 *
	 * @param url          WS的URL地址
	 * @param protocol     协议，见{@link SoapProtocol}
	 * @param namespaceURI 方法上的命名空间URI
	 * @return this
	 * @since 1.0.0
	 */
	public static SoapClient create(String url, SoapProtocol protocol, String namespaceURI) {
		return new SoapClient(url, protocol, namespaceURI);
	}

	/**
	 * 构造，默认使用soap1.1版本协议
	 *
	 * @param url WS的URL地址
	 */
	public SoapClient(String url) {
		this(url, SoapProtocol.SOAP_1_1);
	}

	/**
	 * 构造
	 *
	 * @param url      WS的URL地址
	 * @param protocol 协议版本，见{@link SoapProtocol}
	 */
	public SoapClient(String url, SoapProtocol protocol) {
		this(url, protocol, null);
	}

	/**
	 * 构造
	 *
	 * @param url          WS的URL地址
	 * @param protocol     协议版本，见{@link SoapProtocol}
	 * @param namespaceURI 方法上的命名空间URI
	 * @since 1.0.0
	 */
	public SoapClient(String url, SoapProtocol protocol, String namespaceURI) {
		this.url = url;
		this.namespaceURI = namespaceURI;
		this.protocol = protocol;
		init(protocol);
	}

	/**
	 * 初始化
	 *
	 * @param protocol 协议版本枚举，见{@link SoapProtocol}
	 * @return this
	 */
	public SoapClient init(SoapProtocol protocol) {
		// 创建消息工厂
		try {
			this.factory = MessageFactory.newInstance(protocol.getValue());
			// 根据消息工厂创建SoapMessage
			this.message = factory.createMessage();
		} catch (SOAPException e) {
			throw new SoapRuntimeException(e);
		}

		return this;
	}

	/**
	 * 重置SOAP客户端，用于客户端复用
	 *
	 * <p>
	 * 重置后需调用serMethod方法重新指定请求方法，并调用setParam方法重新定义参数
	 *
	 * @return this
	 * @since 1.0.0
	 */
	public SoapClient reset() {
		try {
			this.message = factory.createMessage();
		} catch (SOAPException e) {
			throw new SoapRuntimeException(e);
		}
		this.methodEle = null;

		return this;
	}

	/**
	 * 设置编码
	 *
	 * @param charset 编码
	 * @return this
	 * @see #charset(Charset)
	 */
	public SoapClient setCharset(Charset charset) {
		return this.charset(charset);
	}

	@Override
	public SoapClient charset(Charset charset) {
		super.charset(charset);
		try {
			this.message.setProperty(SOAPMessage.CHARACTER_SET_ENCODING, this.charset());
			this.message.setProperty(SOAPMessage.WRITE_XML_DECLARATION, "true");
		} catch (SOAPException e) {
			// ignore
		}

		return this;
	}

	/**
	 * 设置Webservice请求地址
	 *
	 * @param url Webservice请求地址
	 * @return this
	 */
	public SoapClient setUrl(String url) {
		this.url = url;
		return this;
	}

	/**
	 * 增加SOAP头信息，方法返回{@link SOAPHeaderElement}可以设置具体属性和子节点
	 *
	 * @param name           头信息标签名
	 * @param actorURI       中间的消息接收者
	 * @param roleUri        Role的URI
	 * @param mustUnderstand 标题项对于要对其进行处理的接收者来说是强制的还是可选的
	 * @param relay          relay属性
	 * @return {@link SOAPHeaderElement}
	 * @since 1.0.0
	 */
	public SOAPHeaderElement addSOAPHeader(QName name, String actorURI, String roleUri, Boolean mustUnderstand, Boolean relay) {
		final SOAPHeaderElement ele = addSOAPHeader(name);
		try {
			if (StrUtil.isNotBlank(roleUri)) {
				ele.setRole(roleUri);
			}
			if (null != relay) {
				ele.setRelay(relay);
			}
		} catch (SOAPException e) {
			throw new SoapRuntimeException(e);
		}

		if (StrUtil.isNotBlank(actorURI)) {
			ele.setActor(actorURI);
		}
		if (null != mustUnderstand) {
			ele.setMustUnderstand(mustUnderstand);
		}

		return ele;
	}

	/**
	 * 增加SOAP头信息，方法返回{@link SOAPHeaderElement}可以设置具体属性和子节点
	 *
	 * @param localName 头节点名称
	 * @return {@link SOAPHeaderElement}
	 * @since 1.0.0
	 */
	public SOAPHeaderElement addSOAPHeader(String localName) {
		return addSOAPHeader(new QName(localName));
	}

	/**
	 * 增加SOAP头信息，方法返回{@link SOAPHeaderElement}可以设置具体属性和子节点
	 *
	 * @param localName 头节点名称
	 * @param value     头节点的值
	 * @return {@link SOAPHeaderElement}
	 * @since 1.0.0
	 */
	public SOAPHeaderElement addSOAPHeader(String localName, String value) {
		final SOAPHeaderElement soapHeaderElement = addSOAPHeader(localName);
		soapHeaderElement.setTextContent(value);
		return soapHeaderElement;
	}

	/**
	 * 增加SOAP头信息，方法返回{@link SOAPHeaderElement}可以设置具体属性和子节点
	 *
	 * @param name 头节点名称
	 * @return {@link SOAPHeaderElement}
	 * @since 1.0.0
	 */
	public SOAPHeaderElement addSOAPHeader(QName name) {
		SOAPHeaderElement ele;
		try {
			ele = this.message.getSOAPHeader().addHeaderElement(name);
		} catch (SOAPException e) {
			throw new SoapRuntimeException(e);
		}
		return ele;
	}

	/**
	 * 设置请求方法
	 *
	 * @param name            方法名及其命名空间
	 * @param params          参数
	 * @param useMethodPrefix 是否使用方法的命名空间前缀
	 * @return this
	 */
	public SoapClient setMethod(Name name, Map<String, Object> params, boolean useMethodPrefix) {
		return setMethod(new QName(name.getURI(), name.getLocalName(), name.getPrefix()), params, useMethodPrefix);
	}

	/**
	 * 设置请求方法
	 *
	 * @param name            方法名及其命名空间
	 * @param params          参数
	 * @param useMethodPrefix 是否使用方法的命名空间前缀
	 * @return this
	 */
	public SoapClient setMethod(QName name, Map<String, Object> params, boolean useMethodPrefix) {
		setMethod(name);
		final String prefix = name.getPrefix();
		final SOAPBodyElement methodEle = this.methodEle;
		for (Entry<String, Object> entry : MapUtil.wrap(params)) {
			setParam(methodEle, entry.getKey(), entry.getValue(), prefix);
		}

		return this;
	}

	/**
	 * 设置请求方法<br>
	 * 方法名自动识别前缀，前缀和方法名使用“:”分隔<br>
	 * 当识别到前缀后，自动添加xmlns属性，关联到默认的namespaceURI
	 *
	 * @param methodName 方法名
	 * @return this
	 */
	public SoapClient setMethod(String methodName) {
		return setMethod(methodName, ObjectUtil.defaultIfNull(this.namespaceURI, XMLConstants.NULL_NS_URI));
	}

	/**
	 * 设置请求方法<br>
	 * 方法名自动识别前缀，前缀和方法名使用“:”分隔<br>
	 * 当识别到前缀后，自动添加xmlns属性，关联到传入的namespaceURI
	 *
	 * @param methodName   方法名（可有前缀也可无）
	 * @param namespaceURI 命名空间URI
	 * @return this
	 */
	public SoapClient setMethod(String methodName, String namespaceURI) {
		final List<String> methodNameList = StrUtil.split(methodName, ':');
		final QName qName;
		if (2 == methodNameList.size()) {
			qName = new QName(namespaceURI, methodNameList.get(1), methodNameList.get(0));
		} else {
			qName = new QName(namespaceURI, methodName);
		}
		return setMethod(qName);
	}

	/**
	 * 设置请求方法
	 *
	 * @param name 方法名及其命名空间
	 * @return this
	 */
	public SoapClient setMethod(QName name) {
		try {
			this.methodEle = this.message.getSOAPBody().addBodyElement(name);
		} catch (SOAPException e) {
			throw new SoapRuntimeException(e);
		}

		return this;
	}

	/**
	 * 设置方法参数，使用方法的前缀
	 *
	 * @param name  参数名
	 * @param value 参数值，可以是字符串或Map或{@link SOAPElement}
	 * @return this
	 */
	public SoapClient setParam(String name, Object value) {
		return setParam(name, value, true);
	}

	/**
	 * 设置方法参数
	 *
	 * @param name            参数名
	 * @param value           参数值，可以是字符串或Map或{@link SOAPElement}
	 * @param useMethodPrefix 是否使用方法的命名空间前缀
	 * @return this
	 */
	public SoapClient setParam(String name, Object value, boolean useMethodPrefix) {
		setParam(this.methodEle, name, value, useMethodPrefix ? this.methodEle.getPrefix() : null);
		return this;
	}

	/**
	 * 批量设置参数，使用方法的前缀
	 *
	 * @param params 参数列表
	 * @return this
	 * @since 1.0.0
	 */
	public SoapClient setParams(Map<String, Object> params) {
		return setParams(params, true);
	}

	/**
	 * 批量设置参数
	 *
	 * @param params          参数列表
	 * @param useMethodPrefix 是否使用方法的命名空间前缀
	 * @return this
	 * @since 1.0.0
	 */
	public SoapClient setParams(Map<String, Object> params, boolean useMethodPrefix) {
		for (Entry<String, Object> entry : MapUtil.wrap(params)) {
			setParam(entry.getKey(), entry.getValue(), useMethodPrefix);
		}
		return this;
	}

	/**
	 * 获取方法节点<br>
	 * 用于创建子节点等操作
	 *
	 * @return {@link SOAPBodyElement}
	 * @since 1.0.0
	 */
	public SOAPBodyElement getMethodEle() {
		return this.methodEle;
	}

	/**
	 * 获取SOAP消息对象 {@link SOAPMessage}
	 *
	 * @return {@link SOAPMessage}
	 * @since 1.0.0
	 */
	public SOAPMessage getMessage() {
		return this.message;
	}

	/**
	 * 获取SOAP请求消息
	 *
	 * @param pretty 是否格式化
	 * @return 消息字符串
	 */
	public String getMsgStr(boolean pretty) {
		return SoapUtil.toString(this.message, pretty, this.charset);
	}

	/**
	 * 将SOAP消息的XML内容输出到流
	 *
	 * @param out 输出流
	 * @return this
	 * @since 1.0.0
	 */
	public SoapClient write(OutputStream out) {
		try {
			this.message.writeTo(out);
		} catch (SOAPException | IOException e) {
			throw new SoapRuntimeException(e);
		}
		return this;
	}

	/**
	 * 设置超时，单位：毫秒<br>
	 * 超时包括：
	 *
	 * <pre>
	 * 1. 连接超时
	 * 2. 读取响应超时
	 * </pre>
	 *
	 * @param milliseconds 超时毫秒数
	 * @return this
	 * @see #setConnectionTimeout(int)
	 * @see #setReadTimeout(int)
	 */
	public SoapClient timeout(int milliseconds) {
		setConnectionTimeout(milliseconds);
		setReadTimeout(milliseconds);
		return this;
	}

	/**
	 * 设置连接超时，单位：毫秒
	 *
	 * @param milliseconds 超时毫秒数
	 * @return this
	 * @since 1.0.0
	 */
	public SoapClient setConnectionTimeout(int milliseconds) {
		this.connectionTimeout = milliseconds;
		return this;
	}

	/**
	 * 设置连接超时，单位：毫秒
	 *
	 * @param milliseconds 超时毫秒数
	 * @return this
	 * @since 1.0.0
	 */
	public SoapClient setReadTimeout(int milliseconds) {
		this.readTimeout = milliseconds;
		return this;
	}

	/**
	 * 执行Webservice请求，即发送SOAP内容
	 *
	 * @return 返回结果
	 */
	public SOAPMessage sendForMessage() {
		final HttpResponse res = sendForResponse();
		final MimeHeaders headers = new MimeHeaders();
		for (Entry<String, List<String>> entry : res.headers().entrySet()) {
			if (StrUtil.isNotEmpty(entry.getKey())) {
				headers.setHeader(entry.getKey(), CollUtil.get(entry.getValue(), 0));
			}
		}
		try {
			return this.factory.createMessage(headers, res.bodyStream());
		} catch (IOException | SOAPException e) {
			throw new SoapRuntimeException(e);
		} finally {
			IoUtil.close(res);
		}
	}

	/**
	 * 执行Webservice请求，即发送SOAP内容
	 *
	 * @return 返回结果
	 */
	public String send() {
		return send(false);
	}

	/**
	 * 执行Webservice请求，即发送SOAP内容
	 *
	 * @param pretty 是否格式化
	 * @return 返回结果
	 */
	public String send(boolean pretty) {
		final String body = sendForResponse().body();
		return pretty ? XmlUtil.format(body) : body;
	}

	// -------------------------------------------------------------------------------------------------------- Private method start

	/**
	 * 发送请求，获取异步响应
	 *
	 * @return 响应对象
	 */
	public HttpResponse sendForResponse() {
		return HttpRequest.post(this.url)//
				.setFollowRedirects(true)//
				.setConnectionTimeout(this.connectionTimeout)
				.setReadTimeout(this.readTimeout)
				.contentType(getXmlContentType())//
				.header(this.headers())
				.body(getMsgStr(false))//
				.executeAsync();
	}

	/**
	 * 获取请求的Content-Type，附加编码信息
	 *
	 * @return 请求的Content-Type
	 */
	private String getXmlContentType() {
		switch (this.protocol){
			case SOAP_1_1:
				return CONTENT_TYPE_SOAP11_TEXT_XML.concat(this.charset.toString());
			case SOAP_1_2:
				return CONTENT_TYPE_SOAP12_SOAP_XML.concat(this.charset.toString());
			default:
				throw new SoapRuntimeException("Unsupported protocol: {}", this.protocol);
		}
	}

	/**
	 * 设置方法参数
	 *
	 * @param ele    方法节点
	 * @param name   参数名
	 * @param value  参数值
	 * @param prefix 命名空间前缀
	 * @return {@link SOAPElement}子节点
	 */
	@SuppressWarnings("rawtypes")
	private static SOAPElement setParam(SOAPElement ele, String name, Object value, String prefix) {
		final SOAPElement childEle;
		try {
			if (StrUtil.isNotBlank(prefix)) {
				childEle = ele.addChildElement(name, prefix);
			} else {
				childEle = ele.addChildElement(name);
			}
		} catch (SOAPException e) {
			throw new SoapRuntimeException(e);
		}

		if (null != value) {
			if (value instanceof SOAPElement) {
				// 单个子节点
				try {
					ele.addChildElement((SOAPElement) value);
				} catch (SOAPException e) {
					throw new SoapRuntimeException(e);
				}
			} else if (value instanceof Map) {
				// 多个字节点
				Entry entry;
				for (Object obj : ((Map) value).entrySet()) {
					entry = (Entry) obj;
					setParam(childEle, entry.getKey().toString(), entry.getValue(), prefix);
				}
			} else {
				// 单个值
				childEle.setValue(value.toString());
			}
		}

		return childEle;
	}
	// -------------------------------------------------------------------------------------------------------- Private method end
}
