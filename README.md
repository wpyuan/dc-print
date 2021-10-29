# dc-print
打印的默认实现。无感知打印，专心关注业务实现的好帮手。

## 配置说明
```yml
# 以下是默认配置，如无更改，不需配置
dc:
  print:
    # 模板目录。默认print/template
    templateClassPath: print/template
    # 模板的字符集。默认UTF-8
    encoding: UTF-8
    # 模板可用字体路径。默认字体：宋体，如以下字体。添加额外字体后，需要在模板对应使用
    fontClassPath:
      - fonts/simsun.ttc
    # 水印
    watermark:
      # 水印字号，默认18号
      fontSize: 18
      # 不透明度 0~1，越靠近0越透明，默认0.125
      opacity: 0.125
    ...
```
更多配置见项目`application-demo.yml`文件

## 接口列表
| 接口描述 | 请求方式 | 接口地址 | 参数说明 |
| --- | --- | ---| ---|
| 浏览器可预览 | `GET` | /print/inline/{code}?businessKey=&title| code：客制化打印处理器标识；businessKey：业务主键；title：文件名称 |
| 下载 | `GET` | /print/down/{code}?businessKey=&title | code：客制化打印处理器标识；businessKey：业务主键；title：文件名称 |
| 批量下载 |`GET`| /print/batch-down/{code}?businessKey=&title| code：客制化打印处理器标识；businessKey：业务主键（可多个）；title：文件名称 |

> 上述接口可选参数：
> - 水印相关：
>  - `boolean` enableWatermark：是否启用水印
>  - `String`  watermarkContent： 水印内容
> - 二维码相关：
>  - `boolean` enableQrcode：是否启用二维码
>  - `String`  qrcodeContent： 二维码内容
>  - `boolean` isQrcodeLocationAllPage：是否所有页都添加二维码，默认否
>  - `List<Interget>` qrcodeLocationPageNumbers：二维码所在页码，默认在首页
>  - `Integer` qrcodeAbsoluteX：二维码所在页面X轴绝对坐标，默认右上角
>  - `Integer` qrcodeAbsoluteY：二维码所在页面Y轴绝对坐标，默认右上角
>  - ... 详情见`com.github.dc.print.pojo.QrcodeConfig`属性

## 使用说明
### 1、引入依赖
[选择版本](https://search.maven.org/artifact/com.github.wpyuan/dc-print)

#### maven举例
```xml
<dependency>
  <groupId>com.github.wpyuan</groupId>
  <artifactId>dc-print</artifactId>
  <version>${latest.version}</version>
</dependency>
```
### 2、编写客制化打印处理器

实现`com.github.dc.print.handler.IPrint<?>`接口，泛型是业务主键类型

```java
@PrintHandler("testHandler")
public class TestPrintHandler implements IPrint<String> {
    
    @Override
    public String template() {
        // tips：指定模板文件名称，*.htm*。目前仅支持单一模板，多模板支持期待后续更新
        ...
    }

    @Override
    public String customFileNameWhenBatchCompress(String businessKey, Map<String, Object> data, String defaultFileName) {
        // tips：指定文件名称，默认是业务主键。对应在批量下载时，定义每个文件的名称，在需要自定义时覆盖实现此方法
       ...
    }

    @Override
    public Map<String, Object> data(String businessKey) {
        // tips：打印所需业务数据，如下面模板title
        ...
    }

    @Override
    public void handleException(String code, String businessKey, OutputStream os, Exception e) {
        // tips：打印过程中出现的异常，在这里进行处理
        ...
    }
}
```

### 3、编写模板
使用`freemarker`和`html`语法编写，设计布局

简单举例
```html
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8"/>
    <title>${title!'print'}</title>
</head>
<body>
${title!'hello world'}
...
</body>
</html>
```

### 4、按场景调用上述接口列表任意接口