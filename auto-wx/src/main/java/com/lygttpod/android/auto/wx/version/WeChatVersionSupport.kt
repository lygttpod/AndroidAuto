package com.lygttpod.android.auto.wx.version

import java.lang.reflect.Proxy

val wechatVersionArray by lazy { WeChatNodesImpl.values().map { it.version }.toList() }

var currentWXVersion = wechatVersionArray[0]

private val wechatNodesImplMap by lazy { WeChatNodesImpl.values().associateBy { it.version } }

val currentWechatNodes: WeChatNodesImpl
    get() = wechatNodesImplMap[currentWXVersion] ?: error("未适配的微信版本, currentWXVersion: $currentWXVersion")

inline fun <reified Nodes> nodeProxy(): Nodes {
    val nodesClass = Nodes::class.java
    val proxy =
        Proxy.newProxyInstance(nodesClass.classLoader, arrayOf(nodesClass)) { _, method, args ->
            val impl = currentWechatNodes
            method.invoke(impl, *args.orEmpty())
        }
    return proxy as Nodes
}