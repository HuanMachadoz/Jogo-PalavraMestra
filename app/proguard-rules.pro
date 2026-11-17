# proguard-rules.pro - Regras de ofuscação para a versão release
# Mantém classes do Retrofit/Gson (não ofusca modelos de dados)
-keep class com.palavramestra.data.remote.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
