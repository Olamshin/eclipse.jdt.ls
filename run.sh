cd org.eclipse.jdt.ls.product/target/repository
export CLIENT_PORT=7080
java \
-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=1044 \
-Declipse.application=org.eclipse.jdt.ls.core.id1 \
-Dosgi.bundles.defaultStartLevel=4 \
-Declipse.product=org.eclipse.jdt.ls.core.product \
-Dlog.protocol=true -Dlog.level=ALL -Xmx1G \
-Djdt.ls.debug=true \
-jar ./plugins/org.eclipse.equinox.launcher_1.6.500.v20230717-2134.jar \
-configuration ./config_mac -data ./jdtls-data