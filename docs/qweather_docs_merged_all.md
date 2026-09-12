# QWeather Docs 合集

> 本文档由自动化合并生成，共包含 105 个子页面。

---


<!-- START_DOC_1: api_air-quality_aqi-list.md (URL: https://dev.qweather.com/docs/api/air-quality/aqi-list) -->
Title: 支持的空气质量指数

URL Source: https://dev.qweather.com/docs/api/air-quality/aqi-list

Published Time: Thu, 27 Aug 2026 02:08:36 GMT

Markdown Content:
## 支持的空气质量指数

和风天气支持两种 AQI 类型，并在 API 中返回最多两个 AQI 数据：通用 AQI 与本地 AQI。

### QAQI[#](https://dev.qweather.com/docs/api/air-quality/aqi-list#qaqi)

**QAQI** 是和风天气定义的通用的空气质量指数，以[世卫组织全球空气质量指南 2021](https://www.who.int/news-room/feature-stories/detail/what-are-the-who-air-quality-guidelines) 为基础并进行了调整，以适应不同国家的自然环境、经济基础和社会状况。

_提示：QAQI 暂时不适用于中国地区。_

### 本地 AQI[#](https://dev.qweather.com/docs/api/air-quality/aqi-list#local-aqi)

本地空气质量一般由各国或地区环境部门进行监控和管理，并且根据当地的实际情况制定空气质量指数的标准，这些指数具有不同的标准和计算方法，并且有可能会发布多个标准的空气质量指数。

### AQI 列表[#](https://dev.qweather.com/docs/api/air-quality/aqi-list#aqi-list)

以下是我们支持的空气质量指数以及它们对应的取值范围、类别等：

| AQI | 取值范围 | (级别) 类别 | 颜色 |
| --- | --- | --- | --- |
| * **AQHI (CA)** * `ca-eccc` * 污染物: o3, no2, pm2p5 | 1 | (1) 低风险 | (0,204,255) |
| 2 | (1) 低风险 | (0,153,204) |
| 3 | (1) 低风险 | (0,102,153) |
| 4 | (2) 中风险 | (255,255,0) |
| 5 | (2) 中风险 | (255,204,0) |
| 6 | (2) 中风险 | (255,153,51) |
| 7 | (3) 高风险 | (255,102,102) |
| 8 | (3) 高风险 | (255,0,0) |
| 9 | (3) 高风险 | (204,0,0) |
| 10 | (3) 高风险 | (153,0,0) |
| 10+ | (4) 极高风险 | (102,0,0) |
| * **AQI (CN)** * `cn-mee` * 污染物: o3, co, so2, no2, pm10, pm2p5 | 0 ~ 50 | (1) 优 | (0,228,0) |
| 51 ~ 100 | (2) 良 | (255,255,0) |
| 101 ~ 150 | (3) 轻度污染 | (255,126,0) |
| 151 ~ 200 | (4) 中度污染 | (255,0,0) |
| 201 ~ 300 | (5) 重度污染 | (153,0,76) |
| 301 ~ 500 | (6) 严重污染 | (126,0,35) |
| * **AQI-1H (CN)** * `cn-mee-1h` * 污染物: o3, co, so2, no2, pm10, pm2p5 | 0 ~ 50 | (1) 优 | (0,228,0) |
| 51 ~ 100 | (2) 良 | (255,255,0) |
| 101 ~ 150 | (3) 轻度污染 | (255,126,0) |
| 151 ~ 200 | (4) 中度污染 | (255,0,0) |
| 201 ~ 300 | (5) 重度污染 | (153,0,76) |
| 301 ~ 500 | (6) 严重污染 | (126,0,35) |
| * **EAQI (EU)** * `eu-eea` * 污染物: o3, so2, no2, pm10, pm2p5 | 1 | (1) 优 | (80,240,230) |
| 2 | (2) 良 | (80,204,170) |
| 3 | (3) 中 | (240,230,65) |
| 4 | (4) 差 | (255,80,80) |
| 5 | (5) 很差 | (150,0,50) |
| 6 | (6) 极差 | (135,33,129) |
| * **Indice ATMO (FR)** * `fr-atmo` * 污染物: o3, so2, no2, pm10, pm2p5 | 1 | (1) 好 | (80,240,230) |
| 2 | (2) 一般 | (80,204,170) |
| 3 | (3) 不好 | (240,230,65) |
| 4 | (4) 差 | (255,80,80) |
| 5 | (5) 很差 | (150,0,50) |
| 6 | (6) 极差 | (135,33,129) |
| * **DAQI (GB)** * `gb-defra` * 污染物: o3, so2, no2, pm10, pm2p5 | 1 | (1) 低 | (156,255,156) |
| 2 | (1) 低 | (49,255,0) |
| 3 | (1) 低 | (49,207,0) |
| 4 | (2) 中 | (255,255,0) |
| 5 | (2) 中 | (255,207,0) |
| 6 | (2) 中 | (255,154,0) |
| 7 | (3) 高 | (255,100,100) |
| 8 | (3) 高 | (255,0,0) |
| 9 | (3) 高 | (153,0,0) |
| 10 | (4) 严重 | (206,48,255) |
| * **AQHI (HK)** * `hk-epd` * 污染物: o3, co, so2, no2, pm10, pm2p5 | 1 ~ 3 | (1) 低 | (77,183,72) |
| 4 ~ 6 | (2) 中 | (249,166,26) |
| 7 | (3) 高 | (237,27,36) |
| 8 ~ 10 | (4) 甚高 | (159,71,33) |
| 10+ | (5) 严重 | (0,0,0) |
| * **AQI (JP)** * `jp-moe` * 污染物: o3, so2, no, no2, nmhc, pm10, pm2p5 | 1 | (1) 蓝色 | (0,51,255) |
| 2 | (2) 青色 | (0,255,255) |
| 3 | (3) 绿色 | (51,255,0) |
| 4 | (4) 黄色/注意 | (255,255,0) |
| 5 | (5) 橙色/警报 | (255,102,0) |
| 6 | (6) 红色/严重警报 | (255,0,0) |
| * **CAI (KR)** * `kr-moe` * 污染物: o3, co, so2, no2, pm10, pm2p5 | 0 ~ 50 | (1) 好 | (0,0,255) |
| 51 ~ 100 | (2) 中等 | (0,255,0) |
| 101 ~ 250 | (3) 不健康 | (255,255,0) |
| 251 ~ 500 | (4) 非常不健康 | (255,0,0) |
| * **AQI (MO)** * `mo-smg` * 污染物: o3, co, so2, no2, pm10, pm2p5 | 0 ~ 50 | (1) 良好 | (38,255,48) |
| 51 ~ 100 | (2) 普通 | (255,255,55) |
| 101 ~ 200 | (3) 不良 | (252,121,34) |
| 201 ~ 300 | (4) 非常不良 | (255,1,0) |
| 301 ~ 400 | (5) 严重 | (227,0,152) |
| 401 ~ 500 | (6) 有害 | (124,0,6) |
| * **QAQI** * `qaqi` * 污染物: o3, co, so2, no2, pm10, pm2p5 | 0 ~ 2.0 | (1) 优 | (32,130,80) |
| 2.1 ~ 4.0 | (2) 良 | (0,91,156) |
| 4.1 ~ 5.0 | (3) 中等 | (242,169,0) |
| 5.1 ~ 7.0 | (4) 差 | (234,107,33) |
| 7.1 ~ 9.0 | (5) 很差 | (202,40,36) |
| 9.1 ~ 10 | (6) 极差 | (128,71,130) |
| * **PSI 24H (SG)** * `sg-nea` * 污染物: o3, co, so2, no2, pm10, pm2p5 | 0 ~ 50 | (1) 良好水平 | (123,196,102) |
| 51 ~ 100 | (2) 适中水平 | (102,186,232) |
| 101 ~ 200 | (3) 不健康水平 | (254,214,49) |
| 201 ~ 300 | (4) 非常不健康水平 | (250,166,53) |
| 301 ~ 500 | (5) 危险水平 | (237,29,47) |
| * **1-Hour PM2.5 (SG)** * `sg-nea-pm1h` * 污染物: pm2p5 | 0 ~ 55 | (1) 等级 1 (正常) | (213,238,252) |
| 56 ~ 150 | (2) 等级2 (偏高) | (188,209,238) |
| 151 ~ 250 | (3) 等级3 (高) | (212,209,233) |
| 251+ | (4) 等级4 (非常高) | (176,172,213) |
| * **AQI (TH)** * `th-pcd` * 污染物: o3, co, so2, no2, pm10, pm2p5 | 0 ~ 25 | (1) 优秀 | (59,204,255) |
| 25 ~ 50 | (2) 良好 | (146,208,80) |
| 51 ~ 100 | (3) 中等 | (255,255,0) |
| 101 ~ 200 | (4) 不健康 | (255,162,0) |
| 201+ | (5) 非常不健康 | (240,70,70) |
| * **Daily AQI (TW)** * `tw-me` * 污染物: o3, co, so2, no2, pm10, pm2p5 | 0 ~ 50 | (1) 良好 | (0,255,0) |
| 51 ~ 100 | (2) 普通 | (255,255,0) |
| 101 ~ 150 | (3) 对敏感人群不健康 | (255,126,0) |
| 151 ~ 200 | (4) 对所有人群不健康 | (255,0,0) |
| 201 ~ 300 | (5) 非常不健康 | (128,0,128) |
| 301 ~ 500 | (6) 危害 | (126,0,35) |
| * **Real-time AQI (TW)** * `tw-me-1h` * 污染物: o3, co, so2, no2, pm10, pm2p5 | 0 ~ 50 | (1) 良好 | (0,255,0) |
| 51 ~ 100 | (2) 普通 | (255,255,0) |
| 101 ~ 150 | (3) 对敏感人群不健康 | (255,126,0) |
| 151 ~ 200 | (4) 对所有人群不健康 | (255,0,0) |
| 201 ~ 300 | (5) 非常不健康 | (128,0,128) |
| 301 ~ 500 | (6) 危害 | (126,0,35) |
| * **AQI (US)** * `us-epa` * 污染物: o3, co, so2, no2, pm10, pm2p5 | 0 ~ 50 | (1) 好 | (0,228,0) |
| 51 ~ 100 | (2) 中等 | (255,255,0) |
| 101 ~ 150 | (3) 不适于敏感人群 | (255,126,0) |
| 151 ~ 200 | (4) 不健康 | (255,0,0) |
| 201 ~ 300 | (5) 非常不健康 | (153,0,76) |
| 301 ~ 500 | (6) 危险 | (126,0,35) |
| * **AQI NowCast (US)** * `us-epa-nc` * 污染物: o3, co, so2, no2, pm10, pm2p5 | 0 ~ 50 | (1) 好 | (0,228,0) |
| 51 ~ 100 | (2) 中等 | (255,255,0) |
| 101 ~ 150 | (3) 不适于敏感人群 | (255,126,0) |
| 151 ~ 200 | (4) 不健康 | (255,0,0) |
| 201 ~ 300 | (5) 非常不健康 | (153,0,76) |
| 301 ~ 500 | (6) 危险 | (126,0,35) |

_下载完整表格：[aqis.csv](https://raw.githubusercontent.com/qwd/dev-site/master/assets/table/aqis.csv)_

<!-- END_DOC_1 -->

---


<!-- START_DOC_2: resource_error-code.md (URL: https://dev.qweather.com/docs/resource/error-code) -->
Title: 错误码

URL Source: https://dev.qweather.com/docs/resource/error-code

Markdown Content:
当请求 API 出现错误时，你会收到对应的错误信息，本文档将介绍和风天气API的错误码和错误信息。

> **注意：** 你应该妥善的处理遇到的错误，当错误发生时，请暂停请求并进行排查。你不应该放任错误的继续发生，否则这些错误的请求看起来是一种DDoS攻击，极端情况下，我们的安全策略可能冻结你的帐号。

错误信息看起来是这个样子：

```
HTTP/2 400
Content-Type: application/problem+json

{
  "error": {
      "status": 400,
      "type": "https://dev.qweather.com/docs/resource/error-code/#invalid-parameters",
      "title": "Invalid Parameters",
      "detail": "Invalid parameters, please check your request.",
      "invalidParams": [
          "lang"
      ]
  }
}
```

## INVALID PARAMETER[#](https://dev.qweather.com/docs/resource/error-code#invalid-parameter)

`HTTP response status code: 400`

错误的参数，一般指的是传入了错误的参数值，具体错误的参数请参考响应中的`error.invalidParams`。

## MISSING PARAMETER[#](https://dev.qweather.com/docs/resource/error-code#missing-parameter)

`HTTP response status code: 400`

缺失参数，当一些必选参数没有传递时将报错，具体缺失的参数请参考响应中的`error.invalidParams`。

## NO SUCH LOCATION[#](https://dev.qweather.com/docs/resource/error-code#no-such-location)

`HTTP response status code: 400`

没有查询到地点信息或不支持的位置，例如查询一个不存在的城市或者一个错误的Location ID，此时你应该检查并更改查询的内容。

## DATA NOT AVAILABLE[#](https://dev.qweather.com/docs/resource/error-code#data-not-available)

`HTTP response status code: 400`

数据暂时不可用。当你查询的数据超过我们支持的范围后将收到此错误码，例如查询一个地点的空气质量，而我们还不支持这个地点的空气质量，请尝试其他地点进行查询。

`HTTP response status code: 401`

身份认证失败，你需要检查你的KEY或Token，考虑到安全因素，我们不会返回具体错误的原因。

## NO CREDIT[#](https://dev.qweather.com/docs/resource/error-code#no-credit)

`HTTP response status code: 403`

你的帐号内没有足够的可用额度、节省计划或其他额度，请求被拒绝。你需要先增加可用额度或购买其他额度之后再继续请求数据。

## OVERDUE[#](https://dev.qweather.com/docs/resource/error-code#overdue)

`HTTP response status code: 403`

由于你帐号内有逾期未支付的账单，请求被拒绝。你需要先完成逾期账单的支付再继续请求数据。

## SECURITY RESTRICTION[#](https://dev.qweather.com/docs/resource/error-code#security-restriction)

`HTTP response status code: 403`

当前请求违反了你设置的请求限制，请求被拒绝，考虑到安全因素，我们不会返回具体违反了哪些请求限制。请检查：

*   该请求是否与你的请求限制有冲突
*   你的请求限制是否合理
*   如果请求不是你发送的，请考虑你的凭据可能已经泄露

## INVALID HOST[#](https://dev.qweather.com/docs/resource/error-code#invalid-host)

`HTTP response status code: 403`

使用了错误的API Host，请求被拒绝。请在[控制台设置](https://console.qweather.com/setting/)中查看自己的API Host。了解[如何创建API请求](https://dev.qweather.com/docs/configuration/api-config/)。

## ACCOUNT SUSPENSION[#](https://dev.qweather.com/docs/resource/error-code#account-suspension)

`HTTP response status code: 403`

由于用户帐号被冻结，请求被拒绝。了解[帐号冻结](https://dev.qweather.com/docs/account/suspension/)。

## DEPRECATED[#](https://dev.qweather.com/docs/resource/error-code#deprecated)

`HTTP response status code: 403`

当前你请求的 API 已经弃用且，请在[开发文档](https://dev.qweather.com/docs/)中查找并使用最新版本。

## FORBIDDEN[#](https://dev.qweather.com/docs/resource/error-code#forbidden)

`HTTP response status code: 403`

你暂时无权限请求这个数据。你可以提交工单向我们了解详情。

## NOT FOUND[#](https://dev.qweather.com/docs/resource/error-code#not-found)

`HTTP response status code: 404`

输入了错误的路径或错误的路径参数，无法找到该资源。

## METHOD NOT ALLOWED[#](https://dev.qweather.com/docs/resource/error-code#method-not-allowed)

`HTTP response status code: 405`

使用了GET以外的方法请求API。

## TOO MANY REQUESTS[#](https://dev.qweather.com/docs/resource/error-code#too-many-requests)

`HTTP response status code: 429`

短时间内请求过多，超过了QPM限制或累积了大量错误请求。你必须等待一段时间或修复错误后再进行重试，否则持续的429状态可能会被认为是滥用服务资源或DDoS攻击，这将导致你的账号被冻结。关于如何设置重试时间，请参考[指数退避算法](https://dev.qweather.com/docs/best-practices/optimize-requests/#using-exponential-backoff-to-handle-errors)。

## OVER MONTHLY LIMIT[#](https://dev.qweather.com/docs/resource/error-code#over-monthly-limit)

`HTTP response status code: 429`

对于包年包月订阅用户，当本月请求量超过限额后将收到此错误码，请等待至下个月再试，或者联系你的商务经理升级订阅方案。

## UNKNOWN ERROR[#](https://dev.qweather.com/docs/resource/error-code#unknown-error)

`HTTP response status code: 500`

我们的服务发生了未知故障，请[提交工单](https://console.qweather.com/support/ticket/new)与我们联系。

<!-- END_DOC_2 -->

---


<!-- START_DOC_3: configuration_api-host.md (URL: https://dev.qweather.com/docs/configuration/api-host) -->
Title: API Host

URL Source: https://dev.qweather.com/docs/configuration/api-host

Markdown Content:
API Host是开发者独立的API地址，用于取代传统的公共API地址，这将提供更高的安全性和保护开发者的隐私。

对于每个开发者帐号来说，API Host都是独立、唯一的，同时API Host也是身份认证的一部分，这意味着即使开发者的凭据泄露了，盗用者如果不知道API Host也是无法请求数据的。

### 查看你的API Host[#](https://dev.qweather.com/docs/configuration/api-host#your-api-host)

你可以在[控制台-设置](https://console.qweather.com/setting)中查看你的API Host。API Host 由系统随机分配，看起来像是：

```
h2a9cf3mhs.xy.qweatherapi.com
```

### 使用API Host[#](https://dev.qweather.com/docs/configuration/api-host#use-api-host)

你需要将 API Host 粘贴至 API 请求 URL，请参考[如何发送API请求](https://dev.qweather.com/docs/configuration/api-config/)。

> **警告：** 如果是使用的是原公共API地址，例如`api.qweather.com`、`devapi.qweather.com`或`geoapi.qweather.com`，请尽快更换为你自己的API Host以提供更高的安全性，[原公共API地址将从2026年起逐步停止服务](https://blog.qweather.com/announce/public-api-domain-change-to-api-host/)。

<!-- END_DOC_3 -->

---


<!-- START_DOC_4: resource_location-list.md (URL: https://dev.qweather.com/docs/resource/location-list) -->
Title: 常见城市列表

URL Source: https://dev.qweather.com/docs/resource/location-list

Markdown Content:
[和风天气开发者服务](https://dev.qweather.com/)

[控制台](https://id.qweather.com/#/login?redirect=https://console.qweather.com)

*   [首页](https://dev.qweather.com/)
*   [文档](https://dev.qweather.com/docs/)
*   [图标](https://icons.qweather.com/)
*   [展示](https://dev.qweather.com/showcase/)
*   [帮助](https://dev.qweather.com/help/)
*   [价格](https://dev.qweather.com/price/)
*   Search K 

*   [文档](https://dev.qweather.com/docs/)
*   >
*   [实用资料](https://dev.qweather.com/docs/resource/)
*   >
*   [常见城市列表](https://dev.qweather.com/docs/resource/location-list/)

#### [开始使用](https://dev.qweather.com/docs/start/)

*   [特性](https://dev.qweather.com/docs/features/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [帐号管理](https://dev.qweather.com/docs/account/)

#### [开发文档](https://dev.qweather.com/docs/api/)

*   [GeoAPI](https://dev.qweather.com/docs/api/geoapi/)
*   [天气预报](https://dev.qweather.com/docs/api/weather/)
*   [分钟预报](https://dev.qweather.com/docs/api/minutely/)
*   [预警](https://dev.qweather.com/docs/api/warning/)
*   [天气指数](https://dev.qweather.com/docs/api/indices/)
*   [空气质量](https://dev.qweather.com/docs/api/air-quality/)
*   [时光机](https://dev.qweather.com/docs/api/time-machine/)
*   [热带气旋（台风）](https://dev.qweather.com/docs/api/tropical-cyclone/)
*   [海洋数据](https://dev.qweather.com/docs/api/ocean/)
*   [太阳辐射](https://dev.qweather.com/docs/api/solar-radiation/)
*   [天文](https://dev.qweather.com/docs/api/astronomy/)
*   [控制台API](https://dev.qweather.com/docs/api/console/)

#### [实用资料](https://dev.qweather.com/docs/resource/)

*   [专用词汇表](https://dev.qweather.com/docs/resource/glossary/)
*   [错误码](https://dev.qweather.com/docs/resource/error-code/)
*   [多语言](https://dev.qweather.com/docs/resource/language/)
*   [常见城市列表](https://dev.qweather.com/docs/resource/location-list/)
*   [元数据](https://dev.qweather.com/docs/resource/metadata/)
*   [单位](https://dev.qweather.com/docs/resource/unit/)

#### [最佳实践](https://dev.qweather.com/docs/best-practices/)

*   [不要假设](https://dev.qweather.com/docs/best-practices/no-assumptions/)
*   [优化请求](https://dev.qweather.com/docs/best-practices/optimize-requests/)
*   [处理Gzip](https://dev.qweather.com/docs/best-practices/gzip/)
*   [缓存你的数据](https://dev.qweather.com/docs/best-practices/cache/)
*   [安全指南](https://dev.qweather.com/docs/best-practices/security-guidelines/)

#### [财务与费用](https://dev.qweather.com/docs/finance/)

*   [计费方式和支付](https://dev.qweather.com/docs/finance/billing-and-payment/)
*   [按量计费定价](https://dev.qweather.com/docs/finance/pricing/)
*   [节省计划](https://dev.qweather.com/docs/finance/savings-plans/)
*   [增值税发票](https://dev.qweather.com/docs/finance/vat-invoice/)

#### [条款](https://dev.qweather.com/docs/terms/)

*   [使用限制](https://dev.qweather.com/docs/terms/restriction/)
*   [注明来源](https://dev.qweather.com/docs/terms/attribution/)
*   [服务条款](https://dev.qweather.com/docs/terms/tos/)

# 常见城市列表

和风天气提供全球15万个城市和多种兴趣点（POI）的天气数据服务，除了通过GeoAPI获取城市和POI信息以外，我们还提供了一些常见城市和POI的列表。

> **提示：** 请优先使用[GeoAPI](https://dev.qweather.com/docs/api/geoapi/)以确保这些信息都是最新的，并可获取更多城市和POI信息。

> **提示：** 城市和POI信息会根据各种原因而变更，因此本列表将不定期更新。

## 下载[#](https://dev.qweather.com/docs/resource/location-list#%e4%b8%8b%e8%bd%bd)

```
git clone https://github.com/qwd/LocationList.git
```

或者[访问我们的Github下载](https://github.com/qwd/LocationList)

### [和风天气开发者服务](https://dev.qweather.com/)

[联系](https://www.qweather.com/contact/)• [博客](https://blog.qweather.com/)• [GitHub](https://github.com/qwd)

Language 

### 文档

*   [开始使用](https://dev.qweather.com/docs/start/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [开发文档](https://dev.qweather.com/docs/api/)
*   [财务与费用](https://dev.qweather.com/docs/finance/)
*   [最佳实践](https://dev.qweather.com/docs/best-practices/)
*   [实用资料](https://dev.qweather.com/docs/resource/)

### 特性

*   [服务和数据](https://dev.qweather.com/docs/features/service-and-data/)
*   [全球部署](https://dev.qweather.com/docs/features/global-deployment/)
*   [性能](https://dev.qweather.com/docs/features/performance/)

### 更多

*   [天气预报](https://www.qweather.com/)
*   [天气地图](https://map.qweather.com/)
*   [下载APP](https://www.qweather.com/app/)

*   © 2026 [QWeather](https://www.qweather.com/en/)
*   [许可协议](https://www.qweather.com/terms/developers-eula/)
*   [隐私政策](https://www.qweather.com/terms/privacy)
*   [免责声明](https://www.qweather.com/terms/disclaimer/)

[京ICP备15048401号-11](https://beian.miit.gov.cn/)

<!-- END_DOC_4 -->

---


<!-- START_DOC_5: api_geoapi.md (URL: https://dev.qweather.com/docs/api/geoapi) -->
Title: GeoAPI

URL Source: https://dev.qweather.com/docs/api/geoapi

Markdown Content:
天气数据是基于地理位置的数据，因此获取天气之前需要先知道具体的位置信息。和风天气提供一个功能强大的位置信息搜索API服务：**GeoAPI**。通过GeoAPI，你可获取到需要查询城市或POI的基本信息，包括查询地区的Location ID、多语言名称、经纬度、时区、海拔、Rank值、归属上级行政区域、所在行政区域等。

除此之外，GeoAPI还可以帮助你：

*   支持名称模糊搜索
*   在应用或网站中根据用户输入的名称返回多个城市结果，便于用户选择准确的城市并返回该城市天气
*   不需要维护城市列表，城市信息更新实时获取

API

[GET 城市搜索](https://dev.qweather.com/docs/api/geoapi/city-lookup/)

搜索全球城市或反查经纬度坐标，并返回位置标识、名称、行政区划和时区等信息。

[GET 热门城市查询](https://dev.qweather.com/docs/api/geoapi/top-city/)

获取全球各国热门城市列表。

[GET POI搜索](https://dev.qweather.com/docs/api/geoapi/poi-lookup/)

使用关键字和坐标查询POI信息。

[GET POI范围搜索](https://dev.qweather.com/docs/api/geoapi/poi-range/)

查询指定区域范围内的全部兴趣点信息。

<!-- END_DOC_5 -->

---


<!-- START_DOC_6: api_geoapi_poi-range.md (URL: https://dev.qweather.com/docs/api/geoapi/poi-range) -->
Title: POI范围搜索

URL Source: https://dev.qweather.com/docs/api/geoapi/poi-range

Markdown Content:
[和风天气开发者服务](https://dev.qweather.com/)

[控制台](https://id.qweather.com/#/login?redirect=https://console.qweather.com)

*   [首页](https://dev.qweather.com/)
*   [文档](https://dev.qweather.com/docs/)
*   [图标](https://icons.qweather.com/)
*   [展示](https://dev.qweather.com/showcase/)
*   [帮助](https://dev.qweather.com/help/)
*   [价格](https://dev.qweather.com/price/)
*   Search K 

*   [文档](https://dev.qweather.com/docs/)
*   >
*   [开发文档](https://dev.qweather.com/docs/api/)
*   >
*   [GeoAPI](https://dev.qweather.com/docs/api/geoapi/)
*   >
*   [POI范围搜索](https://dev.qweather.com/docs/api/geoapi/poi-range/)

#### [开始使用](https://dev.qweather.com/docs/start/)

*   [特性](https://dev.qweather.com/docs/features/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [帐号管理](https://dev.qweather.com/docs/account/)

#### [开发文档](https://dev.qweather.com/docs/api/)

*   [GeoAPI](https://dev.qweather.com/docs/api/geoapi/)

    *   [GET 城市搜索](https://dev.qweather.com/docs/api/geoapi/city-lookup/)
    *   [GET 热门城市查询](https://dev.qweather.com/docs/api/geoapi/top-city/)
    *   [GET POI搜索](https://dev.qweather.com/docs/api/geoapi/poi-lookup/)
    *   [GET POI范围搜索](https://dev.qweather.com/docs/api/geoapi/poi-range/)

*   [天气预报](https://dev.qweather.com/docs/api/weather/)
*   [分钟预报](https://dev.qweather.com/docs/api/minutely/)
*   [预警](https://dev.qweather.com/docs/api/warning/)
*   [天气指数](https://dev.qweather.com/docs/api/indices/)
*   [空气质量](https://dev.qweather.com/docs/api/air-quality/)
*   [时光机](https://dev.qweather.com/docs/api/time-machine/)
*   [热带气旋（台风）](https://dev.qweather.com/docs/api/tropical-cyclone/)
*   [海洋数据](https://dev.qweather.com/docs/api/ocean/)
*   [太阳辐射](https://dev.qweather.com/docs/api/solar-radiation/)
*   [天文](https://dev.qweather.com/docs/api/astronomy/)
*   [控制台API](https://dev.qweather.com/docs/api/console/)

#### [实用资料](https://dev.qweather.com/docs/resource/)

*   [专用词汇表](https://dev.qweather.com/docs/resource/glossary/)
*   [错误码](https://dev.qweather.com/docs/resource/error-code/)
*   [多语言](https://dev.qweather.com/docs/resource/language/)
*   [常见城市列表](https://dev.qweather.com/docs/resource/location-list/)
*   [元数据](https://dev.qweather.com/docs/resource/metadata/)
*   [单位](https://dev.qweather.com/docs/resource/unit/)

#### [最佳实践](https://dev.qweather.com/docs/best-practices/)

*   [不要假设](https://dev.qweather.com/docs/best-practices/no-assumptions/)
*   [优化请求](https://dev.qweather.com/docs/best-practices/optimize-requests/)
*   [处理Gzip](https://dev.qweather.com/docs/best-practices/gzip/)
*   [缓存你的数据](https://dev.qweather.com/docs/best-practices/cache/)
*   [安全指南](https://dev.qweather.com/docs/best-practices/security-guidelines/)

#### [财务与费用](https://dev.qweather.com/docs/finance/)

*   [计费方式和支付](https://dev.qweather.com/docs/finance/billing-and-payment/)
*   [按量计费定价](https://dev.qweather.com/docs/finance/pricing/)
*   [节省计划](https://dev.qweather.com/docs/finance/savings-plans/)
*   [增值税发票](https://dev.qweather.com/docs/finance/vat-invoice/)

#### [条款](https://dev.qweather.com/docs/terms/)

*   [使用限制](https://dev.qweather.com/docs/terms/restriction/)
*   [注明来源](https://dev.qweather.com/docs/terms/attribution/)
*   [服务条款](https://dev.qweather.com/docs/terms/tos/)

# POI范围搜索

提供指定区域范围内查询所有POI信息。 
## 请求路径[#](https://dev.qweather.com/docs/api/geoapi/poi-range#endpoint)

`GET /geo/v2/poi/range`

## 参数[#](https://dev.qweather.com/docs/api/geoapi/poi-range#parameters)

#### 查询参数

*   location 必选 string  需要查询地区的以英文逗号分隔的[经度,纬度坐标](https://dev.qweather.com/docs/resource/glossary/#coordinate)（十进制，最多支持小数点后两位）。例如 `location=116.41,39.92`  
*   type 必选 string  POI类型，可选择搜索某一类型的POI。`scenic` 景点，`TSTA` 潮汐站点  
*   radius number  搜索半径，范围为1-50公里，默认为5公里。  
*   number integer  返回结果的数量，取值范围1-20，默认返回10个结果。  
*   lang string  [多语言设置](https://dev.qweather.com/docs/resource/language/)  

## 请求示例[#](https://dev.qweather.com/docs/api/geoapi/poi-range#request-example)

```bash
curl -X GET --compressed \
-H 'Authorization: Bearer your_token' \
'https://your-api-host/geo/v2/poi/range?location=116.41%2C39.92&type=scenic'
```

请将`your_token`替换为你的[JWT身份认证](https://dev.qweather.com/docs/configuration/authentication/)，将`your_api_host`替换为你的[API Host](https://dev.qweather.com/docs/configuration/api-host/)

[试一下](https://dev.qweather.com/api-explore/#/Geo/getGeoPoirange)

## 返回数据[#](https://dev.qweather.com/docs/api/geoapi/poi-range#response)

```json
{
  "code": "200",
  "poi": [
    {
      "name": "中山公园",
      "id": "10101010016A",
      "lat": "39.90999985",
      "lon": "116.38999939",
      "adm2": "北京",
      "adm1": "北京",
      "country": "中国",
      "tz": "Asia/Shanghai",
      "utcOffset": "+08:00",
      "isDst": "0",
      "type": "scenic",
      "rank": "86",
      "fxLink": "https://www.qweather.com"
    },
    {
      "name": "故宫博物院",
      "id": "10101010018A",
      "lat": "39.90999985",
      "lon": "116.38999939",
      "adm2": "北京",
      "adm1": "北京",
      "country": "中国",
      "tz": "Asia/Shanghai",
      "utcOffset": "+08:00",
      "isDst": "0",
      "type": "scenic",
      "rank": "67",
      "fxLink": "https://www.qweather.com"
    },
    {
      "name": "北京市规划展览馆",
      "id": "10101010002A",
      "lat": "39.88999939",
      "lon": "116.40000153",
      "adm2": "北京",
      "adm1": "北京",
      "country": "中国",
      "tz": "Asia/Shanghai",
      "utcOffset": "+08:00",
      "isDst": "0",
      "type": "scenic",
      "rank": "68",
      "fxLink": "https://www.qweather.com"
    },
    {
      "name": "老舍茶馆",
      "id": "10101010021A",
      "lat": "39.88999939",
      "lon": "116.38999939",
      "adm2": "北京",
      "adm1": "北京",
      "country": "中国",
      "tz": "Asia/Shanghai",
      "utcOffset": "+08:00",
      "isDst": "0",
      "type": "scenic",
      "rank": "86",
      "fxLink": "https://www.qweather.com"
    },
    {
      "name": "景山公园",
      "id": "10101010012A",
      "lat": "39.91999817",
      "lon": "116.38999939",
      "adm2": "北京",
      "adm1": "北京",
      "country": "中国",
      "tz": "Asia/Shanghai",
      "utcOffset": "+08:00",
      "isDst": "0",
      "type": "scenic",
      "rank": "67",
      "fxLink": "https://www.qweather.com"
    }
  ],
  "refer": {
    "sources": [
      "https://developer.qweather.com/attribution.html"
    ],
    "license": [
      "QWeather Developers License"
    ]
  }
}
```

*   code string  [状态码](https://dev.qweather.com/docs/resource/error-code/)  
*   poi object  POI 列表  
    *   name string  位置名称  
    *   id string  位置ID  
    *   lat string  纬度  
    *   lon string  经度  
    *   adm2 string  上级行政区划名称  
    *   adm1 string  一级行政区域名称  
    *   country string  国家名称  
    *   tz string  [时区](https://dev.qweather.com/docs/resource/glossary/#timezone)  
    *   utcOffset string  当前位置与[UTC时间偏移的小时数](https://dev.qweather.com/docs/resource/glossary/#utc-offset)  
    *   isDst string  是否处于[夏令时](https://dev.qweather.com/docs/resource/glossary/#daylight-saving-time)。`1` 表示当前处于夏令时，`0` 表示当前不是夏令时  
    *   type string  位置的属性  
    *   rank string  [位置的评分](https://dev.qweather.com/docs/resource/glossary/#rank)  
    *   fxLink uri  该位置的天气预报网页链接，便于嵌入你的网站或应用  

*   refer object  数据来源和许可信息  
    *   sources array  原始数据来源，或数据源说明，可能为空  

    *   license array  数据许可或版权声明，可能为空  

#### 本页导航

*   [请求路径](https://dev.qweather.com/docs/api/geoapi/poi-range#endpoint)
*   [参数](https://dev.qweather.com/docs/api/geoapi/poi-range#parameters)
*   [请求示例](https://dev.qweather.com/docs/api/geoapi/poi-range#request-example)
*   [返回数据](https://dev.qweather.com/docs/api/geoapi/poi-range#response)

### [和风天气开发者服务](https://dev.qweather.com/)

[联系](https://www.qweather.com/contact/)• [博客](https://blog.qweather.com/)• [GitHub](https://github.com/qwd)

Language 

### 文档

*   [开始使用](https://dev.qweather.com/docs/start/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [开发文档](https://dev.qweather.com/docs/api/)
*   [财务与费用](https://dev.qweather.com/docs/finance/)
*   [最佳实践](https://dev.qweather.com/docs/best-practices/)
*   [实用资料](https://dev.qweather.com/docs/resource/)

### 特性

*   [服务和数据](https://dev.qweather.com/docs/features/service-and-data/)
*   [全球部署](https://dev.qweather.com/docs/features/global-deployment/)
*   [性能](https://dev.qweather.com/docs/features/performance/)

### 更多

*   [天气预报](https://www.qweather.com/)
*   [天气地图](https://map.qweather.com/)
*   [下载APP](https://www.qweather.com/app/)

*   © 2026 [QWeather](https://www.qweather.com/en/)
*   [许可协议](https://www.qweather.com/terms/developers-eula/)
*   [隐私政策](https://www.qweather.com/terms/privacy)
*   [免责声明](https://www.qweather.com/terms/disclaimer/)

[京ICP备15048401号-11](https://beian.miit.gov.cn/)

<!-- END_DOC_6 -->

---


<!-- START_DOC_7: api_geoapi_poi-lookup.md (URL: https://dev.qweather.com/docs/api/geoapi/poi-lookup) -->
Title: POI搜索

URL Source: https://dev.qweather.com/docs/api/geoapi/poi-lookup

Published Time: Thu, 27 Aug 2026 02:08:36 GMT

Markdown Content:
使用关键字和坐标查询POI信息。 
## 请求路径[#](https://dev.qweather.com/docs/api/geoapi/poi-lookup#endpoint)

`GET /geo/v2/poi/lookup`

## 参数[#](https://dev.qweather.com/docs/api/geoapi/poi-lookup#parameters)

#### 查询参数

*   location 必选

string 
需要查询地区的名称、[LocationID](https://dev.qweather.com/docs/resource/glossary/#locationid)或以英文逗号分隔的[经度,纬度坐标](https://dev.qweather.com/docs/resource/glossary/#coordinate)（十进制），LocationID可通过[GeoAPI](https://dev.qweather.com/docs/api/geoapi/)获取。例如 `location=101010100` 或 `location=116.41,39.92` 
*   type 必选

string 
POI类型，可选择搜索某一类型的POI。`scenic` 景点，`TSTA` 潮汐站点 
*   city

string 
选择POI所在城市，可设定只搜索在特定城市内的POI信息。城市名称可以是文字或城市的LocationID。城市名称为精准匹配，建议使用LocaitonID，如文字无法匹配，则数据返回为空。**默认不限制特定城市** 
*   number

integer 
返回结果的数量，取值范围1-20，默认返回10个结果。 
*   lang

string 

## 请求示例[#](https://dev.qweather.com/docs/api/geoapi/poi-lookup#request-example)

```
curl -X GET --compressed \
-H 'Authorization: Bearer your_token' \
'https://your-api-host/geo/v2/poi/lookup?location=116.41%2C39.92&type=scenic'
```

请将`your_token`替换为你的[JWT身份认证](https://dev.qweather.com/docs/configuration/authentication/)，将`your_api_host`替换为你的[API Host](https://dev.qweather.com/docs/configuration/api-host/)

[试一下](https://dev.qweather.com/api-explore/#/Geo/getGeoPoilookup)

## 返回数据[#](https://dev.qweather.com/docs/api/geoapi/poi-lookup#response)

*   code

string 
*   poi

object 
POI 列表 
    *   name

string 
位置名称 
    *   id

string 
位置ID 
    *   lat

string 
纬度 
    *   lon

string 
经度 
    *   adm2

string 
上级行政区划名称 
    *   adm1

string 
一级行政区域名称 
    *   country

string 
国家名称 
    *   tz

string 
    *   utcOffset

string 
    *   isDst

string 
是否处于[夏令时](https://dev.qweather.com/docs/resource/glossary/#daylight-saving-time)。`1` 表示当前处于夏令时，`0` 表示当前不是夏令时 
    *   type

string 
位置的属性 
    *   rank

string 
    *   fxLink

uri 
该位置的天气预报网页链接，便于嵌入你的网站或应用 

*   refer

object 
数据来源和许可信息 
    *   sources

array 
原始数据来源，或数据源说明，可能为空 

    *   license

array 
数据许可或版权声明，可能为空

<!-- END_DOC_7 -->

---


<!-- START_DOC_8: configuration_sdk-config.md (URL: https://dev.qweather.com/docs/configuration/sdk-config) -->
Title: SDK 配置

URL Source: https://dev.qweather.com/docs/configuration/sdk-config

Markdown Content:
[和风天气开发者服务](https://dev.qweather.com/)

[控制台](https://id.qweather.com/#/login?redirect=https://console.qweather.com)

*   [首页](https://dev.qweather.com/)
*   [文档](https://dev.qweather.com/docs/)
*   [图标](https://icons.qweather.com/)
*   [展示](https://dev.qweather.com/showcase/)
*   [帮助](https://dev.qweather.com/help/)
*   [价格](https://dev.qweather.com/price/)
*   Search K 

*   [文档](https://dev.qweather.com/docs/)
*   >
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   >
*   [SDK 配置](https://dev.qweather.com/docs/configuration/sdk-config/)

#### [开始使用](https://dev.qweather.com/docs/start/)

*   [特性](https://dev.qweather.com/docs/features/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)

    *   [项目和凭据](https://dev.qweather.com/docs/configuration/project-and-key/)
    *   [API Host](https://dev.qweather.com/docs/configuration/api-host/)
    *   [身份认证](https://dev.qweather.com/docs/configuration/authentication/)
    *   [构建 API 请求](https://dev.qweather.com/docs/configuration/api-config/)
    *   [SDK 配置](https://dev.qweather.com/docs/configuration/sdk-config/)

*   [帐号管理](https://dev.qweather.com/docs/account/)

#### [开发文档](https://dev.qweather.com/docs/api/)

*   [GeoAPI](https://dev.qweather.com/docs/api/geoapi/)
*   [天气预报](https://dev.qweather.com/docs/api/weather/)
*   [分钟预报](https://dev.qweather.com/docs/api/minutely/)
*   [预警](https://dev.qweather.com/docs/api/warning/)
*   [天气指数](https://dev.qweather.com/docs/api/indices/)
*   [空气质量](https://dev.qweather.com/docs/api/air-quality/)
*   [时光机](https://dev.qweather.com/docs/api/time-machine/)
*   [热带气旋（台风）](https://dev.qweather.com/docs/api/tropical-cyclone/)
*   [海洋数据](https://dev.qweather.com/docs/api/ocean/)
*   [太阳辐射](https://dev.qweather.com/docs/api/solar-radiation/)
*   [天文](https://dev.qweather.com/docs/api/astronomy/)
*   [控制台API](https://dev.qweather.com/docs/api/console/)

#### [实用资料](https://dev.qweather.com/docs/resource/)

*   [专用词汇表](https://dev.qweather.com/docs/resource/glossary/)
*   [错误码](https://dev.qweather.com/docs/resource/error-code/)
*   [多语言](https://dev.qweather.com/docs/resource/language/)
*   [常见城市列表](https://dev.qweather.com/docs/resource/location-list/)
*   [元数据](https://dev.qweather.com/docs/resource/metadata/)
*   [单位](https://dev.qweather.com/docs/resource/unit/)

#### [最佳实践](https://dev.qweather.com/docs/best-practices/)

*   [不要假设](https://dev.qweather.com/docs/best-practices/no-assumptions/)
*   [优化请求](https://dev.qweather.com/docs/best-practices/optimize-requests/)
*   [处理Gzip](https://dev.qweather.com/docs/best-practices/gzip/)
*   [缓存你的数据](https://dev.qweather.com/docs/best-practices/cache/)
*   [安全指南](https://dev.qweather.com/docs/best-practices/security-guidelines/)

#### [财务与费用](https://dev.qweather.com/docs/finance/)

*   [计费方式和支付](https://dev.qweather.com/docs/finance/billing-and-payment/)
*   [按量计费定价](https://dev.qweather.com/docs/finance/pricing/)
*   [节省计划](https://dev.qweather.com/docs/finance/savings-plans/)
*   [增值税发票](https://dev.qweather.com/docs/finance/vat-invoice/)

#### [条款](https://dev.qweather.com/docs/terms/)

*   [使用限制](https://dev.qweather.com/docs/terms/restriction/)
*   [注明来源](https://dev.qweather.com/docs/terms/attribution/)
*   [服务条款](https://dev.qweather.com/docs/terms/tos/)

# SDK 配置

和风天气开发者服务还提供了 iOS SDK 和 Android SDK 以便于接入天气服务，移动端 SDK 的安装、配置和使用文档在对应的 GitHub 仓库中维护。请选择你的开发平台，前往 GitHub 查看最新的配置方法、示例代码和版本说明：

*   [iOS SDK 配置文档](https://github.com/qwd/qweather-ios-sdk/blob/main/README-zh.md)
*   [Android SDK 配置文档](https://github.com/qwd/qweather-android-sdk/blob/main/README-zh.md)

#### 本页导航

### [和风天气开发者服务](https://dev.qweather.com/)

[联系](https://www.qweather.com/contact/)• [博客](https://blog.qweather.com/)• [GitHub](https://github.com/qwd)

Language 

### 文档

*   [开始使用](https://dev.qweather.com/docs/start/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [开发文档](https://dev.qweather.com/docs/api/)
*   [财务与费用](https://dev.qweather.com/docs/finance/)
*   [最佳实践](https://dev.qweather.com/docs/best-practices/)
*   [实用资料](https://dev.qweather.com/docs/resource/)

### 特性

*   [服务和数据](https://dev.qweather.com/docs/features/service-and-data/)
*   [全球部署](https://dev.qweather.com/docs/features/global-deployment/)
*   [性能](https://dev.qweather.com/docs/features/performance/)

### 更多

*   [天气预报](https://www.qweather.com/)
*   [天气地图](https://map.qweather.com/)
*   [下载APP](https://www.qweather.com/app/)

*   © 2026 [QWeather](https://www.qweather.com/en/)
*   [许可协议](https://www.qweather.com/terms/developers-eula/)
*   [隐私政策](https://www.qweather.com/terms/privacy)
*   [免责声明](https://www.qweather.com/terms/disclaimer/)

[京ICP备15048401号-11](https://beian.miit.gov.cn/)

<!-- END_DOC_8 -->

---


<!-- START_DOC_9: best-practices_security-guidelines.md (URL: https://dev.qweather.com/docs/best-practices/security-guidelines) -->
Title: 安全指南

URL Source: https://dev.qweather.com/docs/best-practices/security-guidelines

Markdown Content:
凭据（包括API KEY和Token）是获取和风天气数据的重要敏感信息，你有责任妥善保管，避免泄露凭据而造成的损失，在这里介绍几种的安全保护凭据的方式。

## 使用HTTPS[#](https://dev.qweather.com/docs/best-practices/security-guidelines#https)

从2014年开始，我们的API就支持了HTTPS加密访问，在2016年底，我们关闭了所有非HTTPS的访问。使用HTTPS可以有效的阻止请求中的敏感信息被泄露，请不要跳过HTTPS的保护。

## 使用JWT身份验证[#](https://dev.qweather.com/docs/best-practices/security-guidelines#authentication-with-jwt)

使用[JWT](https://dev.qweather.com/docs/configuration/authentication/#json-web-token/)进行身份认证，这将有效的保护你的密钥，即使他人抓取到了你的请求信息，也几乎无法获得更多有价值的内容。

## 设置API限制[#](https://dev.qweather.com/docs/best-practices/security-guidelines#api-restrictions)

为你的凭据开启API限制，只有加入选中列表的API才允许请求，默认情况下，凭据可以请求任何API。

如要设置API限制，请访问[控制台-项目管理](https://console.qweather.com/project)，选择需要的项目和凭据。

## 设置应用限制[#](https://dev.qweather.com/docs/best-practices/security-guidelines#app-restrictions)

建议你始终为凭据设置应用限制，加强凭据的安全等级。应用限制采用白名单形式，只有加入限制列表的应用才能使用凭据请求API。

支持的应用限制包括：

*   网址限制
*   IP限制
*   iOS应用限制
*   Andorid应用限制

如要设置应用限制，请访问[控制台-项目管理](https://console.qweather.com/project)，选择需要的项目和凭据。

> **提示：** 每个凭据仅限设置一种应用限制。

### 网址限制[#](https://dev.qweather.com/docs/best-practices/security-guidelines#websites)

最多可以添加10个网址允许使用凭据请求API。如果留空，则允许所有来源网址访问。

*   **不需要**输入协议，例如`https://`或`http://`，仅输入`yourdomain.com`即可。
*   支持根域名：`yourdoamin.com`，请注意根域名不包括www.yourdomain.com
*   支持子域名：`sub.yourdomain.com` 或 `www.yourdomain.com`
*   支持子域名使用通配符：`*.yourdomain.com`，请注意子域名通配符不能与其他字符组合使用，且只能在开头使用。例如： 
    *   ✅ `*.yourdomain.com`
    *   ✅ `*.abc.yourdomain.com`
    *   ❌ `*abc.yourdomain.com`
    *   ❌ `abc.*.yourdomain.com`

*   **不支持**端口、路径和参数： 
    *   ❌`www.yourdomain.com:8080`
    *   ❌`www.yourdomain.com/path/`
    *   ❌`www.yourdomain.com?params`

### IP限制[#](https://dev.qweather.com/docs/best-practices/security-guidelines#ips)

最多可以添加10个IP地址允许使用凭据请求API。如果留空，则允许所有IP地址访问。

*   支持IPv4: `192.168.0.1`
*   支持IPv6: `2001:db8::1`
*   支持CIDR表示子网：`192.168.0.0/12` 或 `2001:db8::/64`

### iOS应用限制[#](https://dev.qweather.com/docs/best-practices/security-guidelines#ios-apps)

最多可添加10个iOS应用，你需要输入每个iOS应用的Bundle ID。如果留空，则允许所有iOS应用使用当前凭据。

如果使用官方iOS SDK则无需额外设置，如果使用第三方SDK或使用API请求数据，你需要在请求头部添加如下参数：

*   `X-iOS-Bundle-Id` iOS应用的Bundle ID

示例：

```
curl -X GET --compressed \
-H 'Authorization: Bearer your_token' \
-H 'X-iOS-Bundle-Id: com.company.appname' \
'https://abcxyz.qweatherapi.com/path/to/data'
```

### Android应用限制[#](https://dev.qweather.com/docs/best-practices/security-guidelines#android-apps)

最多可添加10个Android应用，你需要输入的每个Android应用的Package name和签名证书SHA-1指纹（[如何获取](https://developers.google.cn/android/guides/client-auth?hl=zh-cn#using_keytool_on_the_certificate)）。如果留空，则允许所有Android应用使用当前凭据。

如果使用官方Android SDK则无需额外设置，如果使用第三方SDK或API，你需要在请求头部添加如下参数：

*   `X-Android-Package-Name` Android应用的包名（Package name）
*   `X-Android-Cert` Android应用的签名证书SHA-1指纹

示例：

```
curl -X GET --compressed \
-H 'Authorization: Bearer your_token' \
-H 'X-Android-Package-Name: com.company.appname' \
-H 'X-Android-Cert: 8E:57:D3:9E:E8:DE:D4:1A:E7:E8:47:41:0F:38:D5:4C:54:CA:4C:4A' \
'https://abcxyz.qweatherapi.com/path/to/data'
```

## 使用密钥管理服务存储凭据[#](https://dev.qweather.com/docs/best-practices/security-guidelines#using-kms-to-store-credentials)

大部分云服务商都支持密钥管理服务(KMS, Key Management Service)，你可以使用KMS安全的存储和读取API KEY或Private KEY，以避免在程序编码中泄露KEY。

## 创建多个的凭据[#](https://dev.qweather.com/docs/best-practices/security-guidelines#create-multiple-credentials)

我们支持创建多个项目和凭据，因此不要把鸡蛋都放在一个篮子里，对于不同的产品或者同一产品的不同平台，应该创建不同凭据进行使用，例如你要做一个款关于旅游的项目，可以为iOS、Android、小程序和网站创建不同的凭据。

## 更改凭据[#](https://dev.qweather.com/docs/best-practices/security-guidelines#change-credential)

如果你认为你的凭据已经泄露或者怀疑泄露，你可以新建一个凭据，然后升级你的程序，测试并确保程序都运行在新的凭据，最后删除旧的凭据。

## 删除未使用的凭据[#](https://dev.qweather.com/docs/best-practices/security-guidelines#delete-unused-keys)

如果有不再使用的凭据，请及时删除，避免在程序代码中被错误的使用。你可以查看这个凭据的统计数据，如果长期没有请求量，一般情况是可以放心删除的。

## 使用身份认证服务器[#](https://dev.qweather.com/docs/best-practices/security-guidelines#using-authentication-server)

当前端或客户端应用使用[JWT](https://dev.qweather.com/docs/configuration/authentication/#json-web-token/)进行身份认证时，我们推荐你将Private KEY存储在一台安全的认证服务器中，由认证服务器向你的应用颁发Token，然后再通过应用请求和风天气的数据。

**请始终确保应用与身份认证服务器的连接是安全的。**

## 使用代理服务器[#](https://dev.qweather.com/docs/best-practices/security-guidelines#using-proxy-server)

对于前端或客户端应用，你可以设置一台代理服务器，API KEY或Private KEY储存在代理服务器中，你的应用通过代理服务器向我们发送请求。

**请始终确保应用与代理服务器的连接是安全的。**

## 混淆或加密KEY[#](https://dev.qweather.com/docs/best-practices/security-guidelines#encrypt-key)

如果不可避免的在前端或客户端应用中存储凭据，那么也可以通过混淆或加密代码的方式，再配合不定期的更换凭据，从而让他人获取这些数据更加困难。

在开发过程中，不可避免的会与其他人共享你的代码，因此将凭据存储在环境变量或独立的文件中，则可以有效的避免代码共享时泄露你的凭据。如果你的代码会发布在公共源代码管理系统时（例如Github），请确保存储凭据的文件包含在`.gitignore`或其他类似功能的配置文件中，换句话说，让凭据不要保留在源代码管理系统中。

<!-- END_DOC_9 -->

---


<!-- START_DOC_10: finance_pricing.md (URL: https://dev.qweather.com/docs/finance/pricing) -->
Title: 按量计费定价

URL Source: https://dev.qweather.com/docs/finance/pricing

Markdown Content:
和风天气开发服务采用按量计费的定价模式，不需要提前预付大量资金，也不需要为用不到的服务买单，你只需要为你实际使用的部分付费即可。当你停止使用的时候，你不需要再支付任何费用。另外，按量计费的定价是阶梯价格，意味着你使用的越多，单次请求的价格越低。

本文将介绍按量计费的定价和规则。

## 计费标准[#](https://dev.qweather.com/docs/finance/pricing#specifications)

*   **计价单位：** 一次请求。参考[什么是一次请求](https://dev.qweather.com/help/#what-is-a-request)。
*   **账单周期：** 一个自然月
*   **应计费用周期：** 一个小时
*   **阶梯价累进周期：** 一个自然月
*   **最小计费金额：** 0.01元。不足0.01元的按量计费项目以0.01元计费。

> **例如：** 用户每个月实时天气服务的请求量为100万次，那么用户的成本是：(50000次请求 x 0元) + (950000次请求 x 0.0007元) = 665元/月
> 
> 
> _注意: 这是一个近似值，考虑到每小时的实际请求量，最终费用可能略有出入。_

## 定价[#](https://dev.qweather.com/docs/finance/pricing#pricing)

按量计费是阶梯价计费，累进请求量是按照价格分组中的各项数据请求量之总和计算。建议你使用[价格计算器](https://console.qweather.com/price-calculator)，查看计算过程和预估每月成本。

购买[节省计划](https://dev.qweather.com/docs/finance/savings-plans/)可以大幅降低成本。

> **提示：** 当你的日均请求量超过100万次（或太阳辐照日均请求超过500次），请发送邮件至 [sales@qweather.com](mailto:sales@qweather.com)，我们的商务专家将为你制定更灵活、更划算的方案。

### 天气和基础服务[#](https://dev.qweather.com/docs/finance/pricing#weather-and-essential-services)

价格适用于下列数据服务: [天气预报](https://dev.qweather.com/docs/api/weather/)[分钟预报](https://dev.qweather.com/docs/api/minutely/)[预警](https://dev.qweather.com/docs/api/warning/)[天气指数](https://dev.qweather.com/docs/api/indices/)[空气质量](https://dev.qweather.com/docs/api/air-quality/)[时光机](https://dev.qweather.com/docs/api/time-machine/)[GeoAPI](https://dev.qweather.com/docs/api/geoapi/)[天文](https://dev.qweather.com/docs/api/astronomy/)[控制台API](https://dev.qweather.com/docs/api/console/)

| 请求量(每月) | 价格(每次请求) |
| --- | --- |
| 0–50000 | CNY 0 |
| 之后的 950000 | CNY 0.0007 |
| 之后的 4000000 | CNY 0.0005 |
| 之后的 5000000 | CNY 0.00035 |
| 之后的 40000000 | CNY 0.00015 |
| 之后的 50000000 | CNY 0.0001 |
| 超过 100000000 | 联系我们 |

### 台风和海洋[#](https://dev.qweather.com/docs/finance/pricing#storm-and-ocean)

价格适用于下列数据服务: [热带气旋（台风）](https://dev.qweather.com/docs/api/tropical-cyclone/)[海洋数据](https://dev.qweather.com/docs/api/ocean/)

| 请求量(每月) | 价格(每次请求) |
| --- | --- |
| 0–1000000 | CNY 0.003 |
| 之后的 4000000 | CNY 0.0025 |
| 之后的 5000000 | CNY 0.0015 |
| 超过 10000000 | 联系我们 |

### 太阳辐照[#](https://dev.qweather.com/docs/finance/pricing#solar-radiation)

价格适用于下列数据服务: [太阳辐射](https://dev.qweather.com/docs/api/solar-radiation/)

| 请求量(每月) | 价格(每次请求) |
| --- | --- |
| 0–100000 | CNY 0.3 |
| 之后的 400000 | CNY 0.2 |
| 超过 500000 | 联系我们 |

<!-- END_DOC_10 -->

---


<!-- START_DOC_11: best-practices_no-assumptions.md (URL: https://dev.qweather.com/docs/best-practices/no-assumptions) -->
Title: 不要假设

URL Source: https://dev.qweather.com/docs/best-practices/no-assumptions

Markdown Content:
天气变化瞬息万变，天气数据也是如此，为了确保你的程序更加强壮，无论任何时候，**请不要假定返回的数据都是完整的，或者对返回数据的长度、范围进行假设**。

天气数据会受到多种因素的影响，并且大多数情况下是完全无法避免的，包括自然和物理现象、行政区划的变更、各国气象部门的发布规则、气象算法的迭代以及我们对数据的升级，这些因素都会导致天气数据的变化。

> **例如：** 当我们的数据源缺少某些数据的时候可能会导致字段缺失；高纬度地区某一天可能会出现没有日出日落的情况；预警类型会增加新的代码；当我们的功能升级时，可能在数据返回中增加新的字段或参数。

因此在你的程序中必须考虑如何处理这些问题。

我们的建议有：

*   定期查看我们发布的公告和最新版本的开发文档
*   不要完全依赖枚举值，对新增、删除和变更的值进行适配
*   对新增、删除和变更的字段进行适配
*   对缺省值、空值提前做好适配
*   不要按顺序或字段的数量进行编码
*   了解数据中的状态码及其含义

一般来说，数据的变化我们会提前发布公告，然而请注意，这些变化不一定会向下兼容，如果你没有做好适配，那么你可能必须升级你的程序才能避免发生错误，另外一些更加紧迫性的变更有可能在无法提前通知你的时候发生。

<!-- END_DOC_11 -->

---


<!-- START_DOC_12: api_console_finance.md (URL: https://dev.qweather.com/docs/api/console/finance) -->
Title: 财务汇总

URL Source: https://dev.qweather.com/docs/api/console/finance

Markdown Content:
## 财务汇总

查询你的财务和计费的汇总信息。

> **提示：** 返回的数据截止至上一个小时或更早时候，它与控制台中显示的数据相比可能有1个小时或更长的延迟。

## 获取权限[#](https://dev.qweather.com/docs/api/console/finance#privileges)

必须由帐号所有者在控制台为凭据开通权限后才可以访问本接口。

1.   [前往控制台-项目管理](https://console.qweather.com/project)
2.   点击需要启用控制台API的凭据
3.   向下滑动至“控制台权限”
4.   勾选“允许访问财务汇总数据”
5.   点击“保存”按钮

## 请求路径[#](https://dev.qweather.com/docs/api/console/finance#endpoint)

`GET /finance/v1/summary`

## 参数[#](https://dev.qweather.com/docs/api/console/finance#parameters)

## 请求示例[#](https://dev.qweather.com/docs/api/console/finance#request-example)

```
curl -X GET --compressed \
-H 'Authorization: Bearer your_token' \
'https://your-api-host/finance/v1/summary'
```

请将`your_token`替换为你的[JWT身份认证](https://dev.qweather.com/docs/configuration/authentication/)，将`your_api_host`替换为你的[API Host](https://dev.qweather.com/docs/configuration/api-host/)

[试一下](https://dev.qweather.com/api-explore/#/Console/getConsoleFinance)

## 返回数据[#](https://dev.qweather.com/docs/api/console/finance#response)

*   metadata

object 
    *   tag

string 
数据唯一标识 
    *   attributions

array 
数据归因信息或声明，必须与当前数据共同显示 

*   asOf

date-time 
当前数据的截止日期 
*   currency

string 
货币代码，包括：`CNY`和`USD` 
*   balance

number 
可用额度 
*   accruedCharges

object 
应计费用 
    *   previousDay

number 
前一天应计费用总额 
    *   thisMonth

number 
本月应计费用总额 
    *   sinceLastBill

number 
从上次出账以来的应计费用总额 

*   pendingBills

array 
待支付时账单列表 
    *   number

string 
账单号 
    *   date

date-time 
账单出账日期 
    *   type

string 
账单的类型 
    *   status

string 
账单的状态，包括：`unpaid`和`overdue` 
    *   amount

number 
账单的总金额 
    *   amountDue

number 
账单剩余应付金额 
    *   dueDate

date-time 
应付日期 

*   availableSavingsPlans

array 
可用的节省计划列表 
    *   billNumber

string 
账单号 
    *   status

string 
节省计划状态，包括 `pending`和`active` 
    *   term

string 
承诺期限 
    *   commitments

number 
承诺金额 
    *   utilized

number 
已用承诺金额 
    *   effectiveTime

date-time 
生效时间 

*   availableResourcePlans

array 
可用的资源包列表 
    *   billNumber

string 
账单号 
    *   status

string 
资源包状态，包括 `pending`和`active` 
    *   requests

number 
总请求量 
    *   utilized

number 
已使用的请求数量 
    *   effectiveTime

date-time 
生效时间

<!-- END_DOC_12 -->

---


<!-- START_DOC_13: finance.md (URL: https://dev.qweather.com/docs/finance) -->
Title: 财务与费用

URL Source: https://dev.qweather.com/docs/finance

Markdown Content:
[和风天气开发者服务](https://dev.qweather.com/)

[控制台](https://id.qweather.com/#/login?redirect=https://console.qweather.com)

*   [首页](https://dev.qweather.com/)
*   [文档](https://dev.qweather.com/docs/)
*   [图标](https://icons.qweather.com/)
*   [展示](https://dev.qweather.com/showcase/)
*   [帮助](https://dev.qweather.com/help/)
*   [价格](https://dev.qweather.com/price/)
*   Search K 

*   [文档](https://dev.qweather.com/docs/)
*   >
*   [财务与费用](https://dev.qweather.com/docs/finance/)

#### [开始使用](https://dev.qweather.com/docs/start/)

*   [特性](https://dev.qweather.com/docs/features/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [帐号管理](https://dev.qweather.com/docs/account/)

#### [开发文档](https://dev.qweather.com/docs/api/)

*   [GeoAPI](https://dev.qweather.com/docs/api/geoapi/)
*   [天气预报](https://dev.qweather.com/docs/api/weather/)
*   [分钟预报](https://dev.qweather.com/docs/api/minutely/)
*   [预警](https://dev.qweather.com/docs/api/warning/)
*   [天气指数](https://dev.qweather.com/docs/api/indices/)
*   [空气质量](https://dev.qweather.com/docs/api/air-quality/)
*   [时光机](https://dev.qweather.com/docs/api/time-machine/)
*   [热带气旋（台风）](https://dev.qweather.com/docs/api/tropical-cyclone/)
*   [海洋数据](https://dev.qweather.com/docs/api/ocean/)
*   [太阳辐射](https://dev.qweather.com/docs/api/solar-radiation/)
*   [天文](https://dev.qweather.com/docs/api/astronomy/)
*   [控制台API](https://dev.qweather.com/docs/api/console/)

#### [实用资料](https://dev.qweather.com/docs/resource/)

*   [专用词汇表](https://dev.qweather.com/docs/resource/glossary/)
*   [错误码](https://dev.qweather.com/docs/resource/error-code/)
*   [多语言](https://dev.qweather.com/docs/resource/language/)
*   [常见城市列表](https://dev.qweather.com/docs/resource/location-list/)
*   [元数据](https://dev.qweather.com/docs/resource/metadata/)
*   [单位](https://dev.qweather.com/docs/resource/unit/)

#### [最佳实践](https://dev.qweather.com/docs/best-practices/)

*   [不要假设](https://dev.qweather.com/docs/best-practices/no-assumptions/)
*   [优化请求](https://dev.qweather.com/docs/best-practices/optimize-requests/)
*   [处理Gzip](https://dev.qweather.com/docs/best-practices/gzip/)
*   [缓存你的数据](https://dev.qweather.com/docs/best-practices/cache/)
*   [安全指南](https://dev.qweather.com/docs/best-practices/security-guidelines/)

#### [财务与费用](https://dev.qweather.com/docs/finance/)

*   [计费方式和支付](https://dev.qweather.com/docs/finance/billing-and-payment/)
*   [按量计费定价](https://dev.qweather.com/docs/finance/pricing/)
*   [节省计划](https://dev.qweather.com/docs/finance/savings-plans/)
*   [增值税发票](https://dev.qweather.com/docs/finance/vat-invoice/)

#### [条款](https://dev.qweather.com/docs/terms/)

*   [使用限制](https://dev.qweather.com/docs/terms/restriction/)
*   [注明来源](https://dev.qweather.com/docs/terms/attribution/)
*   [服务条款](https://dev.qweather.com/docs/terms/tos/)

# 财务与费用

和风天气服务提供了有竞争力、灵活的、简单的定价模式，你只需要为你实际使用的部分付款。本文档介绍了我们的定价模式和其他财务相关事项。对于有较大请求量需求的客户，请与我们的商务联系 sales@qweather.com

[计费方式和支付](https://dev.qweather.com/docs/finance/billing-and-payment/)
和风天气开发服务采用按量计费，了解相关计费规则和支付方式。

[按量计费定价](https://dev.qweather.com/docs/finance/pricing/)
了解按量计费的计费标准、阶梯定价规则，以及不同数据服务的请求价格。

[节省计划](https://dev.qweather.com/docs/finance/savings-plans/)
节省计划可以大幅降低长期使用成本，了解节省计划的折扣方式和购买方法。

[增值税发票](https://dev.qweather.com/docs/finance/vat-invoice/)
了解中国地区用户申请增值税发票的开具流程和相关限制。

### [和风天气开发者服务](https://dev.qweather.com/)

[联系](https://www.qweather.com/contact/)• [博客](https://blog.qweather.com/)• [GitHub](https://github.com/qwd)

Language 

### 文档

*   [开始使用](https://dev.qweather.com/docs/start/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [开发文档](https://dev.qweather.com/docs/api/)
*   [财务与费用](https://dev.qweather.com/docs/finance/)
*   [最佳实践](https://dev.qweather.com/docs/best-practices/)
*   [实用资料](https://dev.qweather.com/docs/resource/)

### 特性

*   [服务和数据](https://dev.qweather.com/docs/features/service-and-data/)
*   [全球部署](https://dev.qweather.com/docs/features/global-deployment/)
*   [性能](https://dev.qweather.com/docs/features/performance/)

### 更多

*   [天气预报](https://www.qweather.com/)
*   [天气地图](https://map.qweather.com/)
*   [下载APP](https://www.qweather.com/app/)

*   © 2026 [QWeather](https://www.qweather.com/en/)
*   [许可协议](https://www.qweather.com/terms/developers-eula/)
*   [隐私政策](https://www.qweather.com/terms/privacy)
*   [免责声明](https://www.qweather.com/terms/disclaimer/)

[京ICP备15048401号-11](https://beian.miit.gov.cn/)

<!-- END_DOC_13 -->

---


<!-- START_DOC_14: api_ocean_tide.md (URL: https://dev.qweather.com/docs/api/ocean/tide) -->
Title: 潮汐

URL Source: https://dev.qweather.com/docs/api/ocean/tide

Markdown Content:
[和风天气开发者服务](https://dev.qweather.com/)

[控制台](https://id.qweather.com/#/login?redirect=https://console.qweather.com)

*   [首页](https://dev.qweather.com/)
*   [文档](https://dev.qweather.com/docs/)
*   [图标](https://icons.qweather.com/)
*   [展示](https://dev.qweather.com/showcase/)
*   [帮助](https://dev.qweather.com/help/)
*   [价格](https://dev.qweather.com/price/)
*   Search K 

*   [文档](https://dev.qweather.com/docs/)
*   >
*   [开发文档](https://dev.qweather.com/docs/api/)
*   >
*   [海洋数据](https://dev.qweather.com/docs/api/ocean/)
*   >
*   [潮汐](https://dev.qweather.com/docs/api/ocean/tide/)

#### [开始使用](https://dev.qweather.com/docs/start/)

*   [特性](https://dev.qweather.com/docs/features/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [帐号管理](https://dev.qweather.com/docs/account/)

#### [开发文档](https://dev.qweather.com/docs/api/)

*   [GeoAPI](https://dev.qweather.com/docs/api/geoapi/)
*   [天气预报](https://dev.qweather.com/docs/api/weather/)
*   [分钟预报](https://dev.qweather.com/docs/api/minutely/)
*   [预警](https://dev.qweather.com/docs/api/warning/)
*   [天气指数](https://dev.qweather.com/docs/api/indices/)
*   [空气质量](https://dev.qweather.com/docs/api/air-quality/)
*   [时光机](https://dev.qweather.com/docs/api/time-machine/)
*   [热带气旋（台风）](https://dev.qweather.com/docs/api/tropical-cyclone/)
*   [海洋数据](https://dev.qweather.com/docs/api/ocean/)

    *   [GET 潮汐](https://dev.qweather.com/docs/api/ocean/tide/)

*   [太阳辐射](https://dev.qweather.com/docs/api/solar-radiation/)
*   [天文](https://dev.qweather.com/docs/api/astronomy/)
*   [控制台API](https://dev.qweather.com/docs/api/console/)

#### [实用资料](https://dev.qweather.com/docs/resource/)

*   [专用词汇表](https://dev.qweather.com/docs/resource/glossary/)
*   [错误码](https://dev.qweather.com/docs/resource/error-code/)
*   [多语言](https://dev.qweather.com/docs/resource/language/)
*   [常见城市列表](https://dev.qweather.com/docs/resource/location-list/)
*   [元数据](https://dev.qweather.com/docs/resource/metadata/)
*   [单位](https://dev.qweather.com/docs/resource/unit/)

#### [最佳实践](https://dev.qweather.com/docs/best-practices/)

*   [不要假设](https://dev.qweather.com/docs/best-practices/no-assumptions/)
*   [优化请求](https://dev.qweather.com/docs/best-practices/optimize-requests/)
*   [处理Gzip](https://dev.qweather.com/docs/best-practices/gzip/)
*   [缓存你的数据](https://dev.qweather.com/docs/best-practices/cache/)
*   [安全指南](https://dev.qweather.com/docs/best-practices/security-guidelines/)

#### [财务与费用](https://dev.qweather.com/docs/finance/)

*   [计费方式和支付](https://dev.qweather.com/docs/finance/billing-and-payment/)
*   [按量计费定价](https://dev.qweather.com/docs/finance/pricing/)
*   [节省计划](https://dev.qweather.com/docs/finance/savings-plans/)
*   [增值税发票](https://dev.qweather.com/docs/finance/vat-invoice/)

#### [条款](https://dev.qweather.com/docs/terms/)

*   [使用限制](https://dev.qweather.com/docs/terms/restriction/)
*   [注明来源](https://dev.qweather.com/docs/terms/attribution/)
*   [服务条款](https://dev.qweather.com/docs/terms/tos/)

# 潮汐

未来10天全球潮汐数据，包括满潮、干潮高度和时间，逐小时潮汐数据。 
## 请求路径[#](https://dev.qweather.com/docs/api/ocean/tide#endpoint)

`GET /v7/ocean/tide`

## 参数[#](https://dev.qweather.com/docs/api/ocean/tide#parameters)

#### 查询参数

*   location 必选 string  潮汐观测站的[LocationID](https://dev.qweather.com/docs/resource/glossary/#locationid)，例如 `location=P66981`。  
*   date 必选 date  选择日期，最多可选择未来10天（包含今天）的数据。日期格式为yyyyMMdd，例如 `date=20200531`  

## 请求示例[#](https://dev.qweather.com/docs/api/ocean/tide#request-example)

```bash
curl -X GET --compressed \
-H 'Authorization: Bearer your_token' \
'https://your-api-host/v7/ocean/tide?location=P66981&date=20200531'
```

请将`your_token`替换为你的[JWT身份认证](https://dev.qweather.com/docs/configuration/authentication/)，将`your_api_host`替换为你的[API Host](https://dev.qweather.com/docs/configuration/api-host/)

[试一下](https://dev.qweather.com/api-explore/#/Ocean/getOceanTide)

## 返回数据[#](https://dev.qweather.com/docs/api/ocean/tide#response)

```json
{
  "code": "200",
  "updateTime": "2021-02-04T05:02+08:00",
  "fxLink": "https://www.qweather.com",
  "tideTable": [
    {
      "fxTime": "2021-02-06T03:48+08:00",
      "height": "2.17",
      "type": "H"
    },
    {
      "fxTime": "2021-02-06T10:12+08:00",
      "height": "0.21",
      "type": "L"
    },
    {
      "fxTime": "2021-02-06T16:53+08:00",
      "height": "2.47",
      "type": "H"
    },
    {
      "fxTime": "2021-02-06T23:22+08:00",
      "height": "0.73",
      "type": "L"
    }
  ],
  "tideHourly": [
    {
      "fxTime": "2021-02-06T00:00+08:00",
      "height": "1.02"
    },
    {
      "fxTime": "2021-02-06T01:00+08:00",
      "height": "1.42"
    },
    {
      "fxTime": "2021-02-06T02:00+08:00",
      "height": "1.82"
    },
    {
      "fxTime": "2021-02-06T03:00+08:00",
      "height": "2.10"
    },
    {
      "fxTime": "2021-02-06T04:00+08:00",
      "height": "2.16"
    },
    {
      "fxTime": "2021-02-06T05:00+08:00",
      "height": "2.01"
    },
    {
      "fxTime": "2021-02-06T06:00+08:00",
      "height": "1.68"
    },
    {
      "fxTime": "2021-02-06T07:00+08:00",
      "height": "1.23"
    },
    {
      "fxTime": "2021-02-06T08:00+08:00",
      "height": "0.77"
    },
    {
      "fxTime": "2021-02-06T09:00+08:00",
      "height": "0.39"
    },
    {
      "fxTime": "2021-02-06T10:00+08:00",
      "height": "0.21"
    },
    {
      "fxTime": "2021-02-06T11:00+08:00",
      "height": "0.29"
    },
    {
      "fxTime": "2021-02-06T12:00+08:00",
      "height": "0.60"
    },
    {
      "fxTime": "2021-02-06T13:00+08:00",
      "height": "1.07"
    },
    {
      "fxTime": "2021-02-06T14:00+08:00",
      "height": "1.60"
    },
    {
      "fxTime": "2021-02-06T15:00+08:00",
      "height": "2.07"
    },
    {
      "fxTime": "2021-02-06T16:00+08:00",
      "height": "2.38"
    },
    {
      "fxTime": "2021-02-06T17:00+08:00",
      "height": "2.47"
    },
    {
      "fxTime": "2021-02-06T18:00+08:00",
      "height": "2.34"
    },
    {
      "fxTime": "2021-02-06T19:00+08:00",
      "height": "2.05"
    },
    {
      "fxTime": "2021-02-06T20:00+08:00",
      "height": "1.66"
    },
    {
      "fxTime": "2021-02-06T21:00+08:00",
      "height": "1.25"
    },
    {
      "fxTime": "2021-02-06T22:00+08:00",
      "height": "0.91"
    },
    {
      "fxTime": "2021-02-06T23:00+08:00",
      "height": "0.74"
    }
  ],
  "refer": {
    "sources": [
      "https://developer.qweather.com/attribution.html"
    ],
    "license": [
      "QWeather Developers License"
    ]
  }
}
```

*   code string  [状态码](https://dev.qweather.com/docs/resource/error-code/)  
*   updateTime date-time  [API的最近更新时间](https://dev.qweather.com/docs/resource/glossary/#update-time)  
*   fxLink uri  当前数据的响应式页面，便于嵌入网站或应用  
*   tideTable array  潮汐表列表  
    *   fxTime date-time  预报时间  
    *   height string  海水高度，单位：米  
    *   type string  满潮（H）或干潮（L）  

*   tideHourly array  小时潮汐数据  
    *   fxTime date-time  预报时间  
    *   height string  海水高度，单位：米  

*   refer object  数据来源和许可信息  
    *   sources array  原始数据来源，或数据源说明，可能为空  

    *   license array  数据许可或版权声明，可能为空  

#### 本页导航

*   [请求路径](https://dev.qweather.com/docs/api/ocean/tide#endpoint)
*   [参数](https://dev.qweather.com/docs/api/ocean/tide#parameters)
*   [请求示例](https://dev.qweather.com/docs/api/ocean/tide#request-example)
*   [返回数据](https://dev.qweather.com/docs/api/ocean/tide#response)

### [和风天气开发者服务](https://dev.qweather.com/)

[联系](https://www.qweather.com/contact/)• [博客](https://blog.qweather.com/)• [GitHub](https://github.com/qwd)

Language 

### 文档

*   [开始使用](https://dev.qweather.com/docs/start/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [开发文档](https://dev.qweather.com/docs/api/)
*   [财务与费用](https://dev.qweather.com/docs/finance/)
*   [最佳实践](https://dev.qweather.com/docs/best-practices/)
*   [实用资料](https://dev.qweather.com/docs/resource/)

### 特性

*   [服务和数据](https://dev.qweather.com/docs/features/service-and-data/)
*   [全球部署](https://dev.qweather.com/docs/features/global-deployment/)
*   [性能](https://dev.qweather.com/docs/features/performance/)

### 更多

*   [天气预报](https://www.qweather.com/)
*   [天气地图](https://map.qweather.com/)
*   [下载APP](https://www.qweather.com/app/)

*   © 2026 [QWeather](https://www.qweather.com/en/)
*   [许可协议](https://www.qweather.com/terms/developers-eula/)
*   [隐私政策](https://www.qweather.com/terms/privacy)
*   [免责声明](https://www.qweather.com/terms/disclaimer/)

[京ICP备15048401号-11](https://beian.miit.gov.cn/)

<!-- END_DOC_14 -->

---


<!-- START_DOC_15: api_weather_weather-daily-forecast-webapi-v7.md (URL: https://dev.qweather.com/docs/api/weather/weather-daily-forecast-webapi-v7) -->
Title: 城市每日预报

URL Source: https://dev.qweather.com/docs/api/weather/weather-daily-forecast-webapi-v7

Markdown Content:
## 城市每日预报

> **注意：** WebAPI v7版本的城市天气预报即将弃用，请使用[每日天气预报 v1](https://dev.qweather.com/docs/api/weather/weather-daily-forecast/)代替。

每日天气预报API，提供全球城市未来3-30天天气预报，包括：日出日落、月升月落、最高最低温度、天气白天和夜间状况、风力、风速、风向、相对湿度、站点气压、降水量、露点温度、紫外线强度、能见度等。

## 请求路径[#](https://dev.qweather.com/docs/api/weather/weather-daily-forecast-webapi-v7#endpoint)

`GET /v7/weather/{days}`

## 参数[#](https://dev.qweather.com/docs/api/weather/weather-daily-forecast-webapi-v7#parameters)

#### 路径参数

*   days 必选

string 
预报天数，支持最多30天预报，可选值：`3d`、`7d`、`10d`、`15d`、`30d` 

#### 查询参数

*   location 必选

string 
需要查询地区的[LocationID](https://dev.qweather.com/docs/resource/glossary/#locationid)或以英文逗号分隔的[经度,纬度坐标](https://dev.qweather.com/docs/resource/glossary/#coordinate)（十进制，最多支持小数点后两位），LocationID可通过[GeoAPI](https://dev.qweather.com/docs/api/geoapi/)获取。例如 `location=101010100` 或 `location=116.41,39.92` 
*   lang

string 

## 请求示例[#](https://dev.qweather.com/docs/api/weather/weather-daily-forecast-webapi-v7#request-example)

```
curl -X GET --compressed \
-H 'Authorization: Bearer your_token' \
'https://your-api-host/v7/weather/3d?location=116.41%2C39.92'
```

请将`your_token`替换为你的[JWT身份认证](https://dev.qweather.com/docs/configuration/authentication/)，将`your_api_host`替换为你的[API Host](https://dev.qweather.com/docs/configuration/api-host/)

[试一下](https://dev.qweather.com/api-explore/#/Weather/getWeatherDailyV7)

## 返回数据[#](https://dev.qweather.com/docs/api/weather/weather-daily-forecast-webapi-v7#response)

*   code

string 
*   updateTime

date-time 
*   fxLink

uri 
当前数据的响应式页面，便于嵌入网站或应用 
*   daily

array 
每日预报数据列表 
    *   fxDate

date 
预报日期 
    *   sunrise

string 
    *   sunset

string 
    *   moonrise

string 
    *   moonset

string 
    *   moonPhase

string 
    *   moonPhaseIcon

string 
    *   tempMax

string 
最高温度 
    *   tempMin

string 
最低温度 
    *   iconDay

string 
    *   textDay

string 
白天天气状况文字描述，包括阴晴雨雪等天气状态的描述 
    *   iconNight

string 
    *   textNight

string 
晚间天气状况文字描述，包括阴晴雨雪等天气状态的描述 
    *   wind360Day

string 
白天[风向](https://dev.qweather.com/docs/api/weather/wind-guide/#wind-direction)360角度 
    *   windDirDay

string 
    *   windScaleDay

string 
    *   windSpeedDay

string 
白天风速，公里/小时 
    *   wind360Night

string 
夜间[风向](https://dev.qweather.com/docs/api/weather/wind-guide/#wind-direction)360角度 
    *   windDirNight

string 
    *   windScaleNight

string 
    *   windSpeedNight

string 
夜间风速，公里/小时 
    *   humidity

string 
相对湿度，百分比数值 
    *   precip

string 
预报当天总降水量，默认单位：毫米 
    *   pressure

string 
站点气压，默认单位：百帕 
    *   uvIndex

string 
紫外线强度指数 
    *   vis

string 
能见度，默认单位：公里 
    *   cloud

string 
云量，百分比数值 

*   refer

object 
数据来源和许可信息 
    *   sources

array 
原始数据来源，或数据源说明，可能为空 

    *   license

array 
数据许可或版权声明，可能为空

<!-- END_DOC_15 -->

---


<!-- START_DOC_16: api_weather_weather-now-webapi-v7.md (URL: https://dev.qweather.com/docs/api/weather/weather-now-webapi-v7) -->
Title: 城市实时天气

URL Source: https://dev.qweather.com/docs/api/weather/weather-now-webapi-v7

Markdown Content:
## 城市实时天气

> **注意：** WebAPI v7版本的城市实时天气即将弃用，请使用[实时天气 v1](https://dev.qweather.com/docs/api/weather/weather-current/)代替。

获取中国3000+市县区和海外50万个城市实时天气数据，包括实时温度、体感温度、风力风向、相对湿度、大气压强、降水量、能见度、露点温度、云量等。

## 请求路径[#](https://dev.qweather.com/docs/api/weather/weather-now-webapi-v7#endpoint)

`GET /v7/weather/now`

## 参数[#](https://dev.qweather.com/docs/api/weather/weather-now-webapi-v7#parameters)

#### 查询参数

*   location 必选

string 
需要查询地区的[LocationID](https://dev.qweather.com/docs/resource/glossary/#locationid)或以英文逗号分隔的[经度,纬度坐标](https://dev.qweather.com/docs/resource/glossary/#coordinate)（十进制，最多支持小数点后两位），LocationID可通过[GeoAPI](https://dev.qweather.com/docs/api/geoapi/)获取。例如 `location=101010100` 或 `location=116.41,39.92` 
*   lang

string 

## 请求示例[#](https://dev.qweather.com/docs/api/weather/weather-now-webapi-v7#request-example)

```
curl -X GET --compressed \
-H 'Authorization: Bearer your_token' \
'https://your-api-host/v7/weather/now?location=116.41%2C39.92'
```

请将`your_token`替换为你的[JWT身份认证](https://dev.qweather.com/docs/configuration/authentication/)，将`your_api_host`替换为你的[API Host](https://dev.qweather.com/docs/configuration/api-host/)

[试一下](https://dev.qweather.com/api-explore/#/Weather/getWeatherNowV7)

## 返回数据[#](https://dev.qweather.com/docs/api/weather/weather-now-webapi-v7#response)

*   code

string 
*   updateTime

date-time 
*   fxLink

uri 
当前数据的响应式页面，便于嵌入网站或应用 
*   now

object 
天气实况数据 
    *   obsTime

date-time 
观测时间 
    *   temp

string 
温度，默认单位：摄氏度 
    *   icon

string 
    *   text

string 
天气状况的文字描述，包括阴晴雨雪等天气状态的描述 
    *   wind360

string 
    *   windDir

string 
    *   windScale

string 
    *   windSpeed

string 
风速，公里/小时 
    *   humidity

string 
相对湿度，百分比数值 
    *   precip

string 
降水量，默认单位：毫米 
    *   pressure

string 
站点气压，默认单位：百帕 
    *   cloud

string 
云量，百分比数值 
    *   dew

string 
露点温度 
    *   feelsLike

string 
体感温度，默认单位：摄氏度 
    *   vis

string 
能见度，默认单位：公里 

*   refer

object 
数据来源和许可信息 
    *   sources

array 
原始数据来源，或数据源说明，可能为空 

    *   license

array 
数据许可或版权声明，可能为空

<!-- END_DOC_16 -->

---


<!-- START_DOC_17: api_geoapi_city-lookup.md (URL: https://dev.qweather.com/docs/api/geoapi/city-lookup) -->
Title: 城市搜索

URL Source: https://dev.qweather.com/docs/api/geoapi/city-lookup

Markdown Content:
## 城市搜索

城市搜索API提供全球地理位位置、全球城市搜索服务，支持经纬度坐标反查、多语言、模糊搜索等功能。

天气数据是基于地理位置的数据，因此获取天气之前需要先知道具体的位置信息。使用城市搜索，可获取到该城市的基本信息，包括城市的Location ID，多语言名称、经纬度、时区、海拔、Rank值、归属上级行政区域、所在行政区域等。

## 请求路径[#](https://dev.qweather.com/docs/api/geoapi/city-lookup#endpoint)

`GET /geo/v2/city/lookup`

## 参数[#](https://dev.qweather.com/docs/api/geoapi/city-lookup#parameters)

#### 查询参数

*   location 必选

string 
需要查询地区的名称、[LocationID](https://dev.qweather.com/docs/resource/glossary/#locationid)或以英文逗号分隔的[经度,纬度坐标](https://dev.qweather.com/docs/resource/glossary/#coordinate)（十进制），LocationID可通过[GeoAPI](https://dev.qweather.com/docs/api/geoapi/)获取。例如 `location=101010100` 或 `location=116.41,39.92` 
*   adm

string 
城市的上级行政区划，可设定只在某个行政区划范围内进行搜索，用于排除重名城市或对结果进行过滤。例如 `adm=beijing` 
*   range

string 
搜索范围，可设定只在某个国家或地区范围内进行搜索，国家和地区名称需使用[ISO 3166 所定义的国家代码](https://dev.qweather.com/docs/resource/glossary/#iso-3166)。如果不设置此参数，搜索范围将在所有城市。例如 `range=cn` 
*   number

integer 
返回结果的数量，取值范围1-20，默认返回10个结果。 
*   lang

string 

## 请求示例[#](https://dev.qweather.com/docs/api/geoapi/city-lookup#request-example)

```
curl -X GET --compressed \
-H 'Authorization: Bearer your_token' \
'https://your-api-host/geo/v2/city/lookup?location=116.41%2C39.92'
```

请将`your_token`替换为你的[JWT身份认证](https://dev.qweather.com/docs/configuration/authentication/)，将`your_api_host`替换为你的[API Host](https://dev.qweather.com/docs/configuration/api-host/)

[试一下](https://dev.qweather.com/api-explore/#/Geo/getGeoCitylookup)

## 返回数据[#](https://dev.qweather.com/docs/api/geoapi/city-lookup#response)

*   code

string 
*   location

array 
地区/城市信息列表 
    *   name

string 
位置名称 
    *   id

string 
位置ID 
    *   lat

string 
纬度 
    *   lon

string 
经度 
    *   adm2

string 
上级行政区划名称 
    *   adm1

string 
一级行政区域名称 
    *   country

string 
国家名称 
    *   tz

string 
    *   utcOffset

string 
    *   isDst

string 
是否处于[夏令时](https://dev.qweather.com/docs/resource/glossary/#daylight-saving-time)。`1` 表示当前处于夏令时，`0` 表示当前不是夏令时 
    *   type

string 
位置的属性 
    *   rank

string 
    *   fxLink

uri 
该位置的天气预报网页链接，便于嵌入你的网站或应用 

*   refer

object 
数据来源和许可信息 
    *   sources

array 
原始数据来源，或数据源说明，可能为空 

    *   license

array 
数据许可或版权声明，可能为空

<!-- END_DOC_17 -->

---


<!-- START_DOC_18: api_weather_weather-hourly-forecast-webapi-v7.md (URL: https://dev.qweather.com/docs/api/weather/weather-hourly-forecast-webapi-v7) -->
Title: 城市小时预报

URL Source: https://dev.qweather.com/docs/api/weather/weather-hourly-forecast-webapi-v7

Markdown Content:
[和风天气开发者服务](https://dev.qweather.com/)

[控制台](https://id.qweather.com/#/login?redirect=https://console.qweather.com)

*   [首页](https://dev.qweather.com/)
*   [文档](https://dev.qweather.com/docs/)
*   [图标](https://icons.qweather.com/)
*   [展示](https://dev.qweather.com/showcase/)
*   [帮助](https://dev.qweather.com/help/)
*   [价格](https://dev.qweather.com/price/)
*   Search K 

*   [文档](https://dev.qweather.com/docs/)
*   >
*   [开发文档](https://dev.qweather.com/docs/api/)
*   >
*   [天气预报](https://dev.qweather.com/docs/api/weather/)
*   >
*   [城市小时预报](https://dev.qweather.com/docs/api/weather/weather-hourly-forecast-webapi-v7/)

#### [开始使用](https://dev.qweather.com/docs/start/)

*   [特性](https://dev.qweather.com/docs/features/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [帐号管理](https://dev.qweather.com/docs/account/)

#### [开发文档](https://dev.qweather.com/docs/api/)

*   [GeoAPI](https://dev.qweather.com/docs/api/geoapi/)
*   [天气预报](https://dev.qweather.com/docs/api/weather/)

    *   [GET 实时天气](https://dev.qweather.com/docs/api/weather/weather-current/)
    *   [GET 每日天气预报](https://dev.qweather.com/docs/api/weather/weather-daily-forecast/)
    *   [GET 小时天气预报](https://dev.qweather.com/docs/api/weather/weather-hourly-forecast/)
    *   [GET 城市实时天气](https://dev.qweather.com/docs/api/weather/weather-now-webapi-v7/)
    *   [GET 城市每日预报](https://dev.qweather.com/docs/api/weather/weather-daily-forecast-webapi-v7/)
    *   [GET 城市小时预报](https://dev.qweather.com/docs/api/weather/weather-hourly-forecast-webapi-v7/)
    *   [天气现象](https://dev.qweather.com/docs/api/weather/weather-conditions/)
    *   [风向和等级](https://dev.qweather.com/docs/api/weather/wind-guide/)

*   [分钟预报](https://dev.qweather.com/docs/api/minutely/)
*   [预警](https://dev.qweather.com/docs/api/warning/)
*   [天气指数](https://dev.qweather.com/docs/api/indices/)
*   [空气质量](https://dev.qweather.com/docs/api/air-quality/)
*   [时光机](https://dev.qweather.com/docs/api/time-machine/)
*   [热带气旋（台风）](https://dev.qweather.com/docs/api/tropical-cyclone/)
*   [海洋数据](https://dev.qweather.com/docs/api/ocean/)
*   [太阳辐射](https://dev.qweather.com/docs/api/solar-radiation/)
*   [天文](https://dev.qweather.com/docs/api/astronomy/)
*   [控制台API](https://dev.qweather.com/docs/api/console/)

#### [实用资料](https://dev.qweather.com/docs/resource/)

*   [专用词汇表](https://dev.qweather.com/docs/resource/glossary/)
*   [错误码](https://dev.qweather.com/docs/resource/error-code/)
*   [多语言](https://dev.qweather.com/docs/resource/language/)
*   [常见城市列表](https://dev.qweather.com/docs/resource/location-list/)
*   [元数据](https://dev.qweather.com/docs/resource/metadata/)
*   [单位](https://dev.qweather.com/docs/resource/unit/)

#### [最佳实践](https://dev.qweather.com/docs/best-practices/)

*   [不要假设](https://dev.qweather.com/docs/best-practices/no-assumptions/)
*   [优化请求](https://dev.qweather.com/docs/best-practices/optimize-requests/)
*   [处理Gzip](https://dev.qweather.com/docs/best-practices/gzip/)
*   [缓存你的数据](https://dev.qweather.com/docs/best-practices/cache/)
*   [安全指南](https://dev.qweather.com/docs/best-practices/security-guidelines/)

#### [财务与费用](https://dev.qweather.com/docs/finance/)

*   [计费方式和支付](https://dev.qweather.com/docs/finance/billing-and-payment/)
*   [按量计费定价](https://dev.qweather.com/docs/finance/pricing/)
*   [节省计划](https://dev.qweather.com/docs/finance/savings-plans/)
*   [增值税发票](https://dev.qweather.com/docs/finance/vat-invoice/)

#### [条款](https://dev.qweather.com/docs/terms/)

*   [使用限制](https://dev.qweather.com/docs/terms/restriction/)
*   [注明来源](https://dev.qweather.com/docs/terms/attribution/)
*   [服务条款](https://dev.qweather.com/docs/terms/tos/)

# 城市小时预报

> **注意：** WebAPI v7版本的城市天气预报即将弃用，请使用[小时天气预报 v1](https://dev.qweather.com/docs/api/weather/weather-hourly-forecast/)代替。

逐小时天气预报API，提供全球城市未来24-168小时逐小时天气预报，包括：温度、天气状况、风力、风速、风向、相对湿度、站点气压、降水概率、露点温度、云量。

## 请求路径[#](https://dev.qweather.com/docs/api/weather/weather-hourly-forecast-webapi-v7#endpoint)

`GET /v7/weather/{hours}`

## 参数[#](https://dev.qweather.com/docs/api/weather/weather-hourly-forecast-webapi-v7#parameters)

#### 路径参数

*   hours 必选 string  预报小时数，支持最多168小时预报，可选值：`24h`、`72h`、`168h`  

#### 查询参数

*   location 必选 string  需要查询地区的[LocationID](https://dev.qweather.com/docs/resource/glossary/#locationid)或以英文逗号分隔的[经度,纬度坐标](https://dev.qweather.com/docs/resource/glossary/#coordinate)（十进制，最多支持小数点后两位），LocationID可通过[GeoAPI](https://dev.qweather.com/docs/api/geoapi/)获取。例如 `location=101010100` 或 `location=116.41,39.92`  
*   lang string  [多语言设置](https://dev.qweather.com/docs/resource/language/)  

## 请求示例[#](https://dev.qweather.com/docs/api/weather/weather-hourly-forecast-webapi-v7#request-example)

```bash
curl -X GET --compressed \
-H 'Authorization: Bearer your_token' \
'https://your-api-host/v7/weather/24h?location=116.41%2C39.92'
```

请将`your_token`替换为你的[JWT身份认证](https://dev.qweather.com/docs/configuration/authentication/)，将`your_api_host`替换为你的[API Host](https://dev.qweather.com/docs/configuration/api-host/)

[试一下](https://dev.qweather.com/api-explore/#/Weather/getWeatherHourlyV7)

## 返回数据[#](https://dev.qweather.com/docs/api/weather/weather-hourly-forecast-webapi-v7#response)

```json
{
  "code": "200",
  "updateTime": "2021-02-16T13:35+08:00",
  "fxLink": "https://www.qweather.com/en/weather/beijing-101010100.html",
  "hourly": [
    {
      "fxTime": "2021-02-16T15:00+08:00",
      "temp": "2",
      "icon": "100",
      "text": "晴",
      "wind360": "335",
      "windDir": "西北风",
      "windScale": "3-4",
      "windSpeed": "20",
      "humidity": "11",
      "pop": "0",
      "precip": "0.0",
      "pressure": "1025",
      "cloud": "0",
      "dew": "-25"
    },
    {
      "fxTime": "2021-02-16T16:00+08:00",
      "temp": "1",
      "icon": "100",
      "text": "晴",
      "wind360": "339",
      "windDir": "西北风",
      "windScale": "3-4",
      "windSpeed": "24",
      "humidity": "11",
      "pop": "0",
      "precip": "0.0",
      "pressure": "1025",
      "cloud": "0",
      "dew": "-26"
    },
    {
      "fxTime": "2021-02-16T17:00+08:00",
      "temp": "0",
      "icon": "100",
      "text": "晴",
      "wind360": "341",
      "windDir": "西北风",
      "windScale": "4-5",
      "windSpeed": "25",
      "humidity": "11",
      "pop": "0",
      "precip": "0.0",
      "pressure": "1026",
      "cloud": "0",
      "dew": "-26"
    },
    {
      "fxTime": "2021-02-16T18:00+08:00",
      "temp": "0",
      "icon": "150",
      "text": "晴",
      "wind360": "344",
      "windDir": "西北风",
      "windScale": "4-5",
      "windSpeed": "25",
      "humidity": "12",
      "pop": "0",
      "precip": "0.0",
      "pressure": "1025",
      "cloud": "0",
      "dew": "-27"
    },
    {
      "fxTime": "2021-02-16T19:00+08:00",
      "temp": "-2",
      "icon": "150",
      "text": "晴",
      "wind360": "349",
      "windDir": "西北风",
      "windScale": "3-4",
      "windSpeed": "24",
      "humidity": "13",
      "pop": "0",
      "precip": "0.0",
      "pressure": "1025",
      "cloud": "0",
      "dew": "-27"
    },
    {
      "fxTime": "2021-02-16T20:00+08:00",
      "temp": "-3",
      "icon": "150",
      "text": "晴",
      "wind360": "353",
      "windDir": "北风",
      "windScale": "3-4",
      "windSpeed": "22",
      "humidity": "14",
      "pop": "0",
      "precip": "0.0",
      "pressure": "1025",
      "cloud": "0",
      "dew": "-27"
    },
    {
      "fxTime": "2021-02-16T21:00+08:00",
      "temp": "-3",
      "icon": "150",
      "text": "晴",
      "wind360": "355",
      "windDir": "北风",
      "windScale": "3-4",
      "windSpeed": "20",
      "humidity": "14",
      "pop": "0",
      "precip": "0.0",
      "pressure": "1026",
      "cloud": "0",
      "dew": "-27"
    },
    {
      "fxTime": "2021-02-16T22:00+08:00",
      "temp": "-4",
      "icon": "150",
      "text": "晴",
      "wind360": "356",
      "windDir": "北风",
      "windScale": "3-4",
      "windSpeed": "18",
      "humidity": "16",
      "pop": "0",
      "precip": "0.0",
      "pressure": "1026",
      "cloud": "0",
      "dew": "-27"
    },
    {
      "fxTime": "2021-02-16T23:00+08:00",
      "temp": "-4",
      "icon": "150",
      "text": "晴",
      "wind360": "356",
      "windDir": "北风",
      "windScale": "3-4",
      "windSpeed": "18",
      "humidity": "16",
      "pop": "0",
      "precip": "0.0",
      "pressure": "1026",
      "cloud": "0",
      "dew": "-27"
    },
    {
      "fxTime": "2021-02-17T00:00+08:00",
      "temp": "-4",
      "icon": "150",
      "text": "晴",
      "wind360": "354",
      "windDir": "北风",
      "windScale": "3-4",
      "windSpeed": "16",
      "humidity": "16",
      "pop": "0",
      "precip": "0.0",
      "pressure": "1027",
      "cloud": "0",
      "dew": "-27"
    },
    {
      "fxTime": "2021-02-17T01:00+08:00",
      "temp": "-4",
      "icon": "150",
      "text": "晴",
      "wind360": "351",
      "windDir": "北风",
      "windScale": "3-4",
      "windSpeed": "16",
      "humidity": "16",
      "pop": "0",
      "precip": "0.0",
      "pressure": "1028",
      "cloud": "0",
      "dew": "-27"
    },
    {
      "fxTime": "2021-02-17T02:00+08:00",
      "temp": "-4",
      "icon": "150",
      "text": "晴",
      "wind360": "350",
      "windDir": "北风",
      "windScale": "3-4",
      "windSpeed": "16",
      "humidity": "16",
      "pop": "0",
      "precip": "0.0",
      "pressure": "1028",
      "cloud": "0",
      "dew": "-27"
    },
    {
      "fxTime": "2021-02-17T03:00+08:00",
      "temp": "-5",
      "icon": "150",
      "text": "晴",
      "wind360": "350",
      "windDir": "北风",
      "windScale": "3-4",
      "windSpeed": "16",
      "humidity": "16",
      "pop": "0",
      "precip": "0.0",
      "pressure": "1028",
      "cloud": "0",
      "dew": "-27"
    },
    {
      "fxTime": "2021-02-17T04:00+08:00",
      "temp": "-5",
      "icon": "150",
      "text": "晴",
      "wind360": "351",
      "windDir": "北风",
      "windScale": "3-4",
      "windSpeed": "16",
      "humidity": "15",
      "pop": "0",
      "precip": "0.0",
      "pressure": "1027",
      "cloud": "0",
      "dew": "-28"
    },
    {
      "fxTime": "2021-02-17T05:00+08:00",
      "temp": "-5",
      "icon": "150",
      "text": "晴",
      "wind360": "352",
      "windDir": "北风",
      "windScale": "3-4",
      "windSpeed": "16",
      "humidity": "14",
      "pop": "0",
      "precip": "0.0",
      "pressure": "1026",
      "cloud": "0",
      "dew": "-29"
    },
    {
      "fxTime": "2021-02-17T06:00+08:00",
      "temp": "-5",
      "icon": "150",
      "text": "晴",
      "wind360": "355",
      "windDir": "北风",
      "windScale": "3-4",
      "windSpeed": "14",
      "humidity": "16",
      "pop": "0",
      "precip": "0.0",
      "pressure": "1025",
      "cloud": "0",
      "dew": "-27"
    },
    {
      "fxTime": "2021-02-17T07:00+08:00",
      "temp": "-7",
      "icon": "150",
      "text": "晴",
      "wind360": "359",
      "windDir": "北风",
      "windScale": "3-4",
      "windSpeed": "16",
      "humidity": "20",
      "pop": "0",
      "precip": "0.0",
      "pressure": "1024",
      "cloud": "0",
      "dew": "-26"
    },
    {
      "fxTime": "2021-02-17T08:00+08:00",
      "temp": "-5",
      "icon": "100",
      "text": "晴",
      "wind360": "1",
      "windDir": "北风",
      "windScale": "3-4",
      "windSpeed": "14",
      "humidity": "19",
      "pop": "0",
      "precip": "0.0",
      "pressure": "1023",
      "cloud": "0",
      "dew": "-26"
    },
    {
      "fxTime": "2021-02-17T09:00+08:00",
      "temp": "-4",
      "icon": "100",
      "text": "晴",
      "wind360": "356",
      "windDir": "北风",
      "windScale": "3-4",
      "windSpeed": "14",
      "humidity": "17",
      "pop": "0",
      "precip": "0.0",
      "pressure": "1023",
      "cloud": "0",
      "dew": "-25"
    },
    {
      "fxTime": "2021-02-17T10:00+08:00",
      "temp": "-1",
      "icon": "100",
      "text": "晴",
      "wind360": "344",
      "windDir": "西北风",
      "windScale": "3-4",
      "windSpeed": "14",
      "humidity": "14",
      "pop": "0",
      "precip": "0.0",
      "pressure": "1024",
      "cloud": "0",
      "dew": "-26"
    },
    {
      "fxTime": "2021-02-17T11:00+08:00",
      "temp": "0",
      "icon": "100",
      "text": "晴",
      "wind360": "333",
      "windDir": "西北风",
      "windScale": "3-4",
      "windSpeed": "14",
      "humidity": "12",
      "pop": "0",
      "precip": "0.0",
      "pressure": "1024",
      "cloud": "0",
      "dew": "-26"
    },
    {
      "fxTime": "2021-02-17T12:00+08:00",
      "temp": "1",
      "icon": "100",
      "text": "晴",
      "wind360": "325",
      "windDir": "西北风",
      "windScale": "3-4",
      "windSpeed": "14",
      "humidity": "10",
      "pop": "0",
      "precip": "0.0",
      "pressure": "1025",
      "cloud": "16",
      "dew": "-28"
    },
    {
      "fxTime": "2021-02-17T13:00+08:00",
      "temp": "2",
      "icon": "100",
      "text": "晴",
      "wind360": "319",
      "windDir": "西北风",
      "windScale": "3-4",
      "windSpeed": "16",
      "humidity": "8",
      "pop": "0",
      "precip": "0.0",
      "pressure": "1025",
      "cloud": "32",
      "dew": "-29"
    },
    {
      "fxTime": "2021-02-17T14:00+08:00",
      "temp": "2",
      "icon": "100",
      "text": "晴",
      "wind360": "313",
      "windDir": "西北风",
      "windScale": "3-4",
      "windSpeed": "16",
      "humidity": "9",
      "pop": "0",
      "precip": "0.0",
      "pressure": "1025",
      "cloud": "48",
      "dew": "-27"
    }
  ],
  "refer": {
    "sources": [
      "https://developer.qweather.com/attribution.html"
    ],
    "license": [
      "QWeather Developers License"
    ]
  }
}
```

*   code string  [状态码](https://dev.qweather.com/docs/resource/error-code/)  
*   updateTime date-time  [API的最近更新时间](https://dev.qweather.com/docs/resource/glossary/#update-time)  
*   fxLink uri  当前数据的响应式页面，便于嵌入网站或应用  
*   hourly object  逐小时数据列表  
    *   fxTime date-time  预报时间  
    *   temp string  温度，默认单位：摄氏度  
    *   icon string  天气状况的[图标代码](https://dev.qweather.com/docs/api/weather/weather-conditions/#icons)，另请参考[天气图标项目](https://icons.qweather.com/)  
    *   text string  天气状况的文字描述，包括阴晴雨雪等天气状态的描述  
    *   wind360 string  [风向](https://dev.qweather.com/docs/api/weather/wind-guide/#wind-direction)360角度  
    *   windDir string  [风向](https://dev.qweather.com/docs/api/weather/wind-guide/#wind-direction)  
    *   windScale string  [风力等级](https://dev.qweather.com/docs/api/weather/wind-guide/#wind-scale)  
    *   windSpeed string  风速，公里/小时  
    *   humidity string  相对湿度，百分比数值  
    *   precip string  降水量，默认单位：毫米  
    *   pressure string  站点气压，默认单位：百帕  
    *   cloud string  云量，百分比数值  
    *   dew string  露点温度  
    *   pop string  逐小时预报降水概率，百分比数值，可能为空  

*   refer object  数据来源和许可信息  
    *   sources array  原始数据来源，或数据源说明，可能为空  

    *   license array  数据许可或版权声明，可能为空  

#### 本页导航

*   [请求路径](https://dev.qweather.com/docs/api/weather/weather-hourly-forecast-webapi-v7#endpoint)
*   [参数](https://dev.qweather.com/docs/api/weather/weather-hourly-forecast-webapi-v7#parameters)
*   [请求示例](https://dev.qweather.com/docs/api/weather/weather-hourly-forecast-webapi-v7#request-example)
*   [返回数据](https://dev.qweather.com/docs/api/weather/weather-hourly-forecast-webapi-v7#response)

### [和风天气开发者服务](https://dev.qweather.com/)

[联系](https://www.qweather.com/contact/)• [博客](https://blog.qweather.com/)• [GitHub](https://github.com/qwd)

Language 

### 文档

*   [开始使用](https://dev.qweather.com/docs/start/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [开发文档](https://dev.qweather.com/docs/api/)
*   [财务与费用](https://dev.qweather.com/docs/finance/)
*   [最佳实践](https://dev.qweather.com/docs/best-practices/)
*   [实用资料](https://dev.qweather.com/docs/resource/)

### 特性

*   [服务和数据](https://dev.qweather.com/docs/features/service-and-data/)
*   [全球部署](https://dev.qweather.com/docs/features/global-deployment/)
*   [性能](https://dev.qweather.com/docs/features/performance/)

### 更多

*   [天气预报](https://www.qweather.com/)
*   [天气地图](https://map.qweather.com/)
*   [下载APP](https://www.qweather.com/app/)

*   © 2026 [QWeather](https://www.qweather.com/en/)
*   [许可协议](https://www.qweather.com/terms/developers-eula/)
*   [隐私政策](https://www.qweather.com/terms/privacy)
*   [免责声明](https://www.qweather.com/terms/disclaimer/)

[京ICP备15048401号-11](https://beian.miit.gov.cn/)

<!-- END_DOC_18 -->

---


<!-- START_DOC_19: best-practices_gzip.md (URL: https://dev.qweather.com/docs/best-practices/gzip) -->
Title: 处理Gzip

URL Source: https://dev.qweather.com/docs/best-practices/gzip

Markdown Content:
和风天气的 API 默认采用 [Gzip](https://www.gnu.org/software/gzip/) 进行压缩，这将极大的减少网络流量，加快请求。

> **提示：** 如果使用 iOS SDK 或 Android SDK，不需要考虑处理Gzip。

对于不同开发语言如何处理 Gzip，我们在这里给出一些官方参考文档，这些文档可能与你当前使用的版本不一致，请注意它们的区别。

## C#[#](https://dev.qweather.com/docs/best-practices/gzip#c)

[https://learn.microsoft.com/zh-cn/dotnet/api/system.io.compression.gzipstream?view=net-6.0](https://learn.microsoft.com/zh-cn/dotnet/api/system.io.compression.gzipstream?view=net-6.0)

## Dart[#](https://dev.qweather.com/docs/best-practices/gzip#dart)

[https://api.dart.cn/stable/2.17.0/dart-io/GZipCodec-class.html](https://api.dart.cn/stable/2.17.0/dart-io/GZipCodec-class.html)

## Go[#](https://dev.qweather.com/docs/best-practices/gzip#go)

[https://pkg.go.dev/compress/gzip](https://pkg.go.dev/compress/gzip)

## Java[#](https://dev.qweather.com/docs/best-practices/gzip#java)

[https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/zip/GZIPInputStream.html](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/zip/GZIPInputStream.html)

## Python[#](https://dev.qweather.com/docs/best-practices/gzip#python)

[https://docs.python.org/zh-cn/3/library/gzip.html](https://docs.python.org/zh-cn/3/library/gzip.html)

## Ruby[#](https://dev.qweather.com/docs/best-practices/gzip#ruby)

[https://ruby-doc.org/stdlib-2.7.0/libdoc/zlib/rdoc/Zlib/GzipReader.html](https://ruby-doc.org/stdlib-2.7.0/libdoc/zlib/rdoc/Zlib/GzipReader.html)

<!-- END_DOC_19 -->

---


<!-- START_DOC_20: resource_unit.md (URL: https://dev.qweather.com/docs/resource/unit) -->
Title: 单位

URL Source: https://dev.qweather.com/docs/resource/unit

Markdown Content:
和风天气在2023年之后发布的所有新版本 API 将仅支持公制单位，并在 API 响应中标记当前所使用的单位。

## 无单位数值[#](https://dev.qweather.com/docs/resource/unit#Unitless)

以下数据类型没有给出单位:

*   无量纲量，例如紫外线指数
*   百分比数据，采用 [0,1] 两位小数表示，例如相对湿度
*   角度数据，默认单位是“度”，例如太阳高度角或风向角度

## 单位列表[#](https://dev.qweather.com/docs/resource/unit#unit-list)

| 数据 | 单位名字 | 单位 |
| --- | --- | --- |
| 温度 | 摄氏度 | °C |
| 风速 | 米/秒 | m/s |
| 阵风风速 | 米/秒 | m/s |
| 能见度 | 米 | m |
| 大气压强 | 百帕 | hPa |
| 降水量 | 毫米 | mm |
| 降水强度 | 毫米/小时 | mm/h |
| 太阳辐照度 | 瓦/平方米 | W/m² |
| 海水高度 | 米 | m |

## 旧版本单位参数[#](https://dev.qweather.com/docs/resource/unit#legacy-unit-parameters)

对于所有 Web API v7 版本，均支持使用查询参数 **unit** 进行单位转换，当数据项不存在英制单位时，统一使用公制单位。

| 单位 | API请求参数 | iOS Unit | Android Unit |
| --- | --- | --- | --- |
| 公制单位 | m | METRIC | METRIC |
| 英制单位 | i | IMPERIAL | IMPERIAL |

### Webapi v7 版本所使用的单位列表[#](https://dev.qweather.com/docs/resource/unit#web-api-v7-unit-list)

| 数据项 | 公制单位 | 英制单位 |
| --- | --- | --- |
| 温度 | 摄氏度 | 华氏度 |
| 风速 | 公里/小时 km/h | 英里/小时 mile/h |
| 能见度 | 公里 km | 英里 mile |
| 大气压强 | 百帕 hPa | 百帕 hPa |
| 降水量 | 毫米 mm | 毫米 mm |

<!-- END_DOC_20 -->

---


<!-- START_DOC_21: resource_language.md (URL: https://dev.qweather.com/docs/resource/language) -->
Title: 多语言

URL Source: https://dev.qweather.com/docs/resource/language

Markdown Content:
和风天气支持30+种主流语言以及所在国家或地区的官方语言。

## 默认语言[#](https://dev.qweather.com/docs/resource/language#default-language)

多语言是可选项，默认值是所在国家或地区的官方语言，如不存在官方语言或有多种官方语言，我们将选择其中最为流行或使用人数较高的语言，例如：

*   纽约：英语
*   蒙特利尔：英语
*   班加罗尔：印地语

## 回退顺序[#](https://dev.qweather.com/docs/resource/language#fallback-order)

如果一些数据无法响应你的语言设置时，将按照下列规则回退到下一个所支持的语言：

**没有设置**多语言时，将使用默认语言，顺序是：

```
官方语言 > 英语
```

**设置**了一个指定语言时，顺序是：

```
指定语言 > 官方语言 > 英语
```

## 例外[#](https://dev.qweather.com/docs/resource/language#exception)

大部分数据支持多语言和官方语言，但是一些数据可能不支持所有的语言选项，请参考下列表格查看哪些数据无法完全支持多语言。

| 数据 | 可用语言 |
| --- | --- |
| [天气指数](https://dev.qweather.com/docs/api/indices/) | 中文，英文 |
| [分钟降水](https://dev.qweather.com/docs/api/minutely/minutely-precipitation/) | 中文，英文 |

## 多语言代码[#](https://dev.qweather.com/docs/resource/language#language-code)

你可以使用查询参数`lang`设置数据的语言，以下是在API或SDK中的多语言代码。

| 语言名称 | API 代码 | iOS Lang | Android Lang |
| --- | --- | --- | --- |
| 简体中文 | zh-hans、zh | ZH_HANS | ZH_HANS |
| 繁体中文 | zh-hant | ZH_HANT | ZH_HANT |
| 英文 | en | EN | EN |
| 德语 | de | DE | DE |
| 西班牙语 | es | ES | ES |
| 法语 | fr | FR | FR |
| 意大利语 | it | IT | IT |
| 日语 | ja | JA | JA |
| 韩语 | ko | KO | KO |
| 俄语 | ru | RU | RU |
| 印地语 | hi | HI | HI |
| 泰语 | th | TH | TH |
| 阿拉伯语 | ar | AR | AR |
| 葡萄牙语 | pt | PT | PT |
| 孟加拉语 | bn | BN | BN |
| 马来语 | ms | MS | MS |
| 荷兰语 | nl | NL | NL |
| 希腊语 | el | EL | EL |
| 拉丁语 | la | LA | LA |
| 瑞典语 | sv | SV | SV |
| 印尼语 | id | ID | ID |
| 波兰语 | pl | PL | PL |
| 土耳其语 | tr | TR | TR |
| 捷克语 | cs | CS | CS |
| 爱沙尼亚语 | et | ET | ET |
| 越南语 | vi | VI | VI |
| 菲律宾语 | fil | FIL | FIL |
| 芬兰语 | fi | FI | FI |
| 希伯来语 | he | HE | HE |
| 冰岛语 | is | IS | IS |
| 挪威语 | nb | NB | NB |

## 示例代码：[#](https://dev.qweather.com/docs/resource/language#%e7%a4%ba%e4%be%8b%e4%bb%a3%e7%a0%81)

Swift

```
let _ = Lang.ZH_HANS
```

Objective-C

```
Lang lang = LangZH_HANS;
```

Java

```
Lang lang = Lang.ZH_HANS;
```

<!-- END_DOC_21 -->

---


<!-- START_DOC_22: api_minutely_minutely-precipitation.md (URL: https://dev.qweather.com/docs/api/minutely/minutely-precipitation) -->
Title: 分钟级降水

URL Source: https://dev.qweather.com/docs/api/minutely/minutely-precipitation

Markdown Content:
[和风天气开发者服务](https://dev.qweather.com/)

[控制台](https://id.qweather.com/#/login?redirect=https://console.qweather.com)

*   [首页](https://dev.qweather.com/)
*   [文档](https://dev.qweather.com/docs/)
*   [图标](https://icons.qweather.com/)
*   [展示](https://dev.qweather.com/showcase/)
*   [帮助](https://dev.qweather.com/help/)
*   [价格](https://dev.qweather.com/price/)
*   Search K 

*   [文档](https://dev.qweather.com/docs/)
*   >
*   [开发文档](https://dev.qweather.com/docs/api/)
*   >
*   [分钟预报](https://dev.qweather.com/docs/api/minutely/)
*   >
*   [分钟级降水](https://dev.qweather.com/docs/api/minutely/minutely-precipitation/)

#### [开始使用](https://dev.qweather.com/docs/start/)

*   [特性](https://dev.qweather.com/docs/features/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [帐号管理](https://dev.qweather.com/docs/account/)

#### [开发文档](https://dev.qweather.com/docs/api/)

*   [GeoAPI](https://dev.qweather.com/docs/api/geoapi/)
*   [天气预报](https://dev.qweather.com/docs/api/weather/)
*   [分钟预报](https://dev.qweather.com/docs/api/minutely/)

    *   [GET 分钟级降水](https://dev.qweather.com/docs/api/minutely/minutely-precipitation/)

*   [预警](https://dev.qweather.com/docs/api/warning/)
*   [天气指数](https://dev.qweather.com/docs/api/indices/)
*   [空气质量](https://dev.qweather.com/docs/api/air-quality/)
*   [时光机](https://dev.qweather.com/docs/api/time-machine/)
*   [热带气旋（台风）](https://dev.qweather.com/docs/api/tropical-cyclone/)
*   [海洋数据](https://dev.qweather.com/docs/api/ocean/)
*   [太阳辐射](https://dev.qweather.com/docs/api/solar-radiation/)
*   [天文](https://dev.qweather.com/docs/api/astronomy/)
*   [控制台API](https://dev.qweather.com/docs/api/console/)

#### [实用资料](https://dev.qweather.com/docs/resource/)

*   [专用词汇表](https://dev.qweather.com/docs/resource/glossary/)
*   [错误码](https://dev.qweather.com/docs/resource/error-code/)
*   [多语言](https://dev.qweather.com/docs/resource/language/)
*   [常见城市列表](https://dev.qweather.com/docs/resource/location-list/)
*   [元数据](https://dev.qweather.com/docs/resource/metadata/)
*   [单位](https://dev.qweather.com/docs/resource/unit/)

#### [最佳实践](https://dev.qweather.com/docs/best-practices/)

*   [不要假设](https://dev.qweather.com/docs/best-practices/no-assumptions/)
*   [优化请求](https://dev.qweather.com/docs/best-practices/optimize-requests/)
*   [处理Gzip](https://dev.qweather.com/docs/best-practices/gzip/)
*   [缓存你的数据](https://dev.qweather.com/docs/best-practices/cache/)
*   [安全指南](https://dev.qweather.com/docs/best-practices/security-guidelines/)

#### [财务与费用](https://dev.qweather.com/docs/finance/)

*   [计费方式和支付](https://dev.qweather.com/docs/finance/billing-and-payment/)
*   [按量计费定价](https://dev.qweather.com/docs/finance/pricing/)
*   [节省计划](https://dev.qweather.com/docs/finance/savings-plans/)
*   [增值税发票](https://dev.qweather.com/docs/finance/vat-invoice/)

#### [条款](https://dev.qweather.com/docs/terms/)

*   [使用限制](https://dev.qweather.com/docs/terms/restriction/)
*   [注明来源](https://dev.qweather.com/docs/terms/attribution/)
*   [服务条款](https://dev.qweather.com/docs/terms/tos/)

# 分钟级降水

分钟级降水API（临近预报）支持中国1公里精度的未来2小时每5分钟降雨预报数据。 
## 请求路径[#](https://dev.qweather.com/docs/api/minutely/minutely-precipitation#endpoint)

`GET /v7/minutely/5m`

## 参数[#](https://dev.qweather.com/docs/api/minutely/minutely-precipitation#parameters)

#### 查询参数

*   location 必选 string  需要查询地区的以英文逗号分隔的[经度,纬度坐标](https://dev.qweather.com/docs/resource/glossary/#coordinate)（十进制，最多支持小数点后两位）。例如 `location=116.41,39.92`  
*   lang string  [多语言设置](https://dev.qweather.com/docs/resource/language/)  

## 请求示例[#](https://dev.qweather.com/docs/api/minutely/minutely-precipitation#request-example)

```bash
curl -X GET --compressed \
-H 'Authorization: Bearer your_token' \
'https://your-api-host/v7/minutely/5m?location=116.41%2C39.92'
```

请将`your_token`替换为你的[JWT身份认证](https://dev.qweather.com/docs/configuration/authentication/)，将`your_api_host`替换为你的[API Host](https://dev.qweather.com/docs/configuration/api-host/)

[试一下](https://dev.qweather.com/api-explore/#/Weather/getMinutelyPrecipitation)

## 返回数据[#](https://dev.qweather.com/docs/api/minutely/minutely-precipitation#response)

```json
{
  "code": "200",
  "updateTime": "2021-12-16T18:55+08:00",
  "fxLink": "https://www.qweather.com",
  "summary": "95分钟后雨就停了",
  "minutely": [
    {
      "fxTime": "2021-12-16T18:55+08:00",
      "precip": "0.15",
      "type": "rain"
    },
    {
      "fxTime": "2021-12-16T19:00+08:00",
      "precip": "0.23",
      "type": "rain"
    },
    {
      "fxTime": "2021-12-16T19:05+08:00",
      "precip": "0.21",
      "type": "rain"
    },
    {
      "fxTime": "2021-12-16T19:10+08:00",
      "precip": "0.17",
      "type": "rain"
    },
    {
      "fxTime": "2021-12-16T19:15+08:00",
      "precip": "0.18",
      "type": "rain"
    },
    {
      "fxTime": "2021-12-16T19:20+08:00",
      "precip": "0.24",
      "type": "rain"
    },
    {
      "fxTime": "2021-12-16T19:25+08:00",
      "precip": "0.31",
      "type": "rain"
    },
    {
      "fxTime": "2021-12-16T19:30+08:00",
      "precip": "0.37",
      "type": "rain"
    },
    {
      "fxTime": "2021-12-16T19:35+08:00",
      "precip": "0.41",
      "type": "rain"
    },
    {
      "fxTime": "2021-12-16T19:40+08:00",
      "precip": "0.43",
      "type": "rain"
    },
    {
      "fxTime": "2021-12-16T19:45+08:00",
      "precip": "0.41",
      "type": "rain"
    },
    {
      "fxTime": "2021-12-16T19:50+08:00",
      "precip": "0.36",
      "type": "rain"
    },
    {
      "fxTime": "2021-12-16T19:55+08:00",
      "precip": "0.32",
      "type": "rain"
    },
    {
      "fxTime": "2021-12-16T20:00+08:00",
      "precip": "0.27",
      "type": "rain"
    },
    {
      "fxTime": "2021-12-16T20:05+08:00",
      "precip": "0.22",
      "type": "rain"
    },
    {
      "fxTime": "2021-12-16T20:10+08:00",
      "precip": "0.17",
      "type": "rain"
    },
    {
      "fxTime": "2021-12-16T20:15+08:00",
      "precip": "0.11",
      "type": "rain"
    },
    {
      "fxTime": "2021-12-16T20:20+08:00",
      "precip": "0.06",
      "type": "rain"
    },
    {
      "fxTime": "2021-12-16T20:25+08:00",
      "precip": "0.0",
      "type": "rain"
    },
    {
      "fxTime": "2021-12-16T20:30+08:00",
      "precip": "0.0",
      "type": "rain"
    },
    {
      "fxTime": "2021-12-16T20:35+08:00",
      "precip": "0.0",
      "type": "rain"
    },
    {
      "fxTime": "2021-12-16T20:40+08:00",
      "precip": "0.0",
      "type": "rain"
    },
    {
      "fxTime": "2021-12-16T20:45+08:00",
      "precip": "0.0",
      "type": "rain"
    },
    {
      "fxTime": "2021-12-16T20:50+08:00",
      "precip": "0.0",
      "type": "rain"
    }
  ],
  "refer": {
    "sources": [
      "https://developer.qweather.com/attribution.html"
    ],
    "license": [
      "QWeather Developers License"
    ]
  }
}
```

*   code string  [状态码](https://dev.qweather.com/docs/resource/error-code/)  
*   updateTime date-time  [API的最近更新时间](https://dev.qweather.com/docs/resource/glossary/#update-time)  
*   fxLink uri  当前数据的响应式页面，便于嵌入网站或应用  
*   summary string  分钟降水描述  
*   minutely array  每分钟降水数据列表  
    *   fxTime date-time  预报时间  
    *   precip string  5分钟累计降水量，单位毫米  
    *   type string  降水类型：`rain` = 雨，`snow` = 雪  

*   refer object  数据来源和许可信息  
    *   sources array  原始数据来源，或数据源说明，可能为空  

    *   license array  数据许可或版权声明，可能为空  

#### 本页导航

*   [请求路径](https://dev.qweather.com/docs/api/minutely/minutely-precipitation#endpoint)
*   [参数](https://dev.qweather.com/docs/api/minutely/minutely-precipitation#parameters)
*   [请求示例](https://dev.qweather.com/docs/api/minutely/minutely-precipitation#request-example)
*   [返回数据](https://dev.qweather.com/docs/api/minutely/minutely-precipitation#response)

### [和风天气开发者服务](https://dev.qweather.com/)

[联系](https://www.qweather.com/contact/)• [博客](https://blog.qweather.com/)• [GitHub](https://github.com/qwd)

Language 

### 文档

*   [开始使用](https://dev.qweather.com/docs/start/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [开发文档](https://dev.qweather.com/docs/api/)
*   [财务与费用](https://dev.qweather.com/docs/finance/)
*   [最佳实践](https://dev.qweather.com/docs/best-practices/)
*   [实用资料](https://dev.qweather.com/docs/resource/)

### 特性

*   [服务和数据](https://dev.qweather.com/docs/features/service-and-data/)
*   [全球部署](https://dev.qweather.com/docs/features/global-deployment/)
*   [性能](https://dev.qweather.com/docs/features/performance/)

### 更多

*   [天气预报](https://www.qweather.com/)
*   [天气地图](https://map.qweather.com/)
*   [下载APP](https://www.qweather.com/app/)

*   © 2026 [QWeather](https://www.qweather.com/en/)
*   [许可协议](https://www.qweather.com/terms/developers-eula/)
*   [隐私政策](https://www.qweather.com/terms/privacy)
*   [免责声明](https://www.qweather.com/terms/disclaimer/)

[京ICP备15048401号-11](https://beian.miit.gov.cn/)

<!-- END_DOC_22 -->

---


<!-- START_DOC_23: api_minutely.md (URL: https://dev.qweather.com/docs/api/minutely) -->
Title: 分钟预报

URL Source: https://dev.qweather.com/docs/api/minutely

Markdown Content:
[和风天气开发者服务](https://dev.qweather.com/)

[控制台](https://id.qweather.com/#/login?redirect=https://console.qweather.com)

*   [首页](https://dev.qweather.com/)
*   [文档](https://dev.qweather.com/docs/)
*   [图标](https://icons.qweather.com/)
*   [展示](https://dev.qweather.com/showcase/)
*   [帮助](https://dev.qweather.com/help/)
*   [价格](https://dev.qweather.com/price/)
*    

*   [文档](https://dev.qweather.com/docs/)
*   >
*   [开发文档](https://dev.qweather.com/docs/api/)
*   >
*   [分钟预报](https://dev.qweather.com/docs/api/minutely/)

#### [开始使用](https://dev.qweather.com/docs/start/)

*   [特性](https://dev.qweather.com/docs/features/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [帐号管理](https://dev.qweather.com/docs/account/)

#### [开发文档](https://dev.qweather.com/docs/api/)

*   [GeoAPI](https://dev.qweather.com/docs/api/geoapi/)
*   [天气预报](https://dev.qweather.com/docs/api/weather/)
*   [分钟预报](https://dev.qweather.com/docs/api/minutely/)

    *   [GET 分钟级降水](https://dev.qweather.com/docs/api/minutely/minutely-precipitation/)

*   [预警](https://dev.qweather.com/docs/api/warning/)
*   [天气指数](https://dev.qweather.com/docs/api/indices/)
*   [空气质量](https://dev.qweather.com/docs/api/air-quality/)
*   [时光机](https://dev.qweather.com/docs/api/time-machine/)
*   [热带气旋（台风）](https://dev.qweather.com/docs/api/tropical-cyclone/)
*   [海洋数据](https://dev.qweather.com/docs/api/ocean/)
*   [太阳辐射](https://dev.qweather.com/docs/api/solar-radiation/)
*   [天文](https://dev.qweather.com/docs/api/astronomy/)
*   [控制台API](https://dev.qweather.com/docs/api/console/)

#### [实用资料](https://dev.qweather.com/docs/resource/)

*   [专用词汇表](https://dev.qweather.com/docs/resource/glossary/)
*   [错误码](https://dev.qweather.com/docs/resource/error-code/)
*   [多语言](https://dev.qweather.com/docs/resource/language/)
*   [常见城市列表](https://dev.qweather.com/docs/resource/location-list/)
*   [元数据](https://dev.qweather.com/docs/resource/metadata/)
*   [单位](https://dev.qweather.com/docs/resource/unit/)

#### [最佳实践](https://dev.qweather.com/docs/best-practices/)

*   [不要假设](https://dev.qweather.com/docs/best-practices/no-assumptions/)
*   [优化请求](https://dev.qweather.com/docs/best-practices/optimize-requests/)
*   [处理Gzip](https://dev.qweather.com/docs/best-practices/gzip/)
*   [缓存你的数据](https://dev.qweather.com/docs/best-practices/cache/)
*   [安全指南](https://dev.qweather.com/docs/best-practices/security-guidelines/)

#### [财务与费用](https://dev.qweather.com/docs/finance/)

*   [计费方式和支付](https://dev.qweather.com/docs/finance/billing-and-payment/)
*   [按量计费定价](https://dev.qweather.com/docs/finance/pricing/)
*   [节省计划](https://dev.qweather.com/docs/finance/savings-plans/)
*   [增值税发票](https://dev.qweather.com/docs/finance/vat-invoice/)

#### [条款](https://dev.qweather.com/docs/terms/)

*   [使用限制](https://dev.qweather.com/docs/terms/restriction/)
*   [注明来源](https://dev.qweather.com/docs/terms/attribution/)
*   [服务条款](https://dev.qweather.com/docs/terms/tos/)

# 分钟预报

分钟级降水API（临近预报）支持中国1公里精度的分钟级降雨预报数据，为每一分钟的降雨进行精准预测。

API

[GET 分钟级降水](https://dev.qweather.com/docs/api/minutely/minutely-precipitation/)
获取中国地区未来2小时、每5分钟更新一次的1公里分辨率降水预报。

### [和风天气开发者服务](https://dev.qweather.com/)

[联系](https://www.qweather.com/contact/)• [博客](https://blog.qweather.com/)• [GitHub](https://github.com/qwd)

Language 

### 文档

*   [开始使用](https://dev.qweather.com/docs/start/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [开发文档](https://dev.qweather.com/docs/api/)
*   [财务与费用](https://dev.qweather.com/docs/finance/)
*   [最佳实践](https://dev.qweather.com/docs/best-practices/)
*   [实用资料](https://dev.qweather.com/docs/resource/)

### 特性

*   [服务和数据](https://dev.qweather.com/docs/features/service-and-data/)
*   [全球部署](https://dev.qweather.com/docs/features/global-deployment/)
*   [性能](https://dev.qweather.com/docs/features/performance/)

### 更多

*   [天气预报](https://www.qweather.com/)
*   [天气地图](https://map.qweather.com/)
*   [下载APP](https://www.qweather.com/app/)

*   © 2026 [QWeather](https://www.qweather.com/en/)
*   [许可协议](https://www.qweather.com/terms/developers-eula/)
*   [隐私政策](https://www.qweather.com/terms/privacy)
*   [免责声明](https://www.qweather.com/terms/disclaimer/)

[京ICP备15048401号-11](https://beian.miit.gov.cn/)

<!-- END_DOC_23 -->

---


<!-- START_DOC_24: api_weather_wind-guide.md (URL: https://dev.qweather.com/docs/api/weather/wind-guide) -->
Title: 风向和等级

URL Source: https://dev.qweather.com/docs/api/weather/wind-guide

Markdown Content:
本篇文档将介绍在天气数据中关于风向、风速和风力等级的一些信息。

## 风向[#](https://dev.qweather.com/docs/api/weather/wind-guide#wind-direction)

风向是指**风吹来的方向**，例如东南风表示从东南方向吹来的风。和风天气使用风向角度和风向方位来描述风向。

### 风向角度[#](https://dev.qweather.com/docs/api/weather/wind-guide#degree-direction)

风向角度以正北为0°，顺时针旋转，取值范围 [0, 359]。如果没有主导风向或风向持续变化，风向角度返回 `null`。

### 风向方位[#](https://dev.qweather.com/docs/api/weather/wind-guide#compass-direction)

风向方位通常采用16个方位（[示意图](https://dl.qweather.com/sites/dev/wind-direction-compass.png)），即将360°平分为16份，正北=0°（360°），正东=90°，正南=180°，正西=270°。

| 方位代码 | 方位描述 | 对应角度(°) | 角度范围(°) |
| --- | --- | --- | --- |
| `n` | 北风 | 0 | 348.75 - 11.25 |
| `nne` | 东北偏北风 | 22.5 | 11.25 - 33.75 |
| `ne` | 东北风 | 45 | 33.75 - 56.25 |
| `ene` | 东北偏东风 | 67.5 | 56.25 - 78.75 |
| `e` | 东风 | 90 | 78.75 - 101.25 |
| `ese` | 东南偏东风 | 112.5 | 101.25 - 123.75 |
| `se` | 东南风 | 135 | 123.75 - 146.25 |
| `sse` | 东南偏南风 | 157.5 | 146.25 - 168.75 |
| `s` | 南风 | 180 | 168.75 - 191.25 |
| `ssw` | 西南偏南风 | 202.5 | 191.25 - 213.75 |
| `sw` | 西南风 | 225 | 213.75 - 236.25 |
| `wsw` | 西南偏西风 | 247.5 | 236.25 - 258.75 |
| `w` | 西风 | 270 | 258.75 - 281.25 |
| `wnw` | 西北偏西风 | 292.5 | 281.25 - 303.75 |
| `nw` | 西北风 | 315 | 303.75 - 326.25 |
| `nnw` | 西北偏北风 | 337.5 | 326.25 - 348.75 |
| `none` | 无持续风向 | null |  |
| `vrb` | 风向变化不定 | null |  |

### 旧版本风向[#](https://dev.qweather.com/docs/api/weather/wind-guide#legacy-wind-direction)

Web API v7 版本的天气预报适用下列风向方位：

> **注意：** 在 `windDir` 字段，如果语言设置为中文，则返回中文方位名称，如果设置为其他语言，则返回方位代码。

| 方位代码 | 方位 | 对应角度(°) | 角度范围(°) |
| --- | --- | --- | --- |
| `N` | 北风 | 0 | 348.75 - 11.25 |
| `NNE` | 东北偏北风 | 22.5 | 11.25 - 33.75 |
| `NE` | 东北风 | 45 | 33.75 - 56.25 |
| `ENE` | 东北偏东风 | 67.5 | 56.25 - 78.75 |
| `E` | 东风 | 90 | 78.75 - 101.25 |
| `ESE` | 东南偏东风 | 112.5 | 101.25 - 123.75 |
| `SE` | 东南风 | 135 | 123.75 - 146.25 |
| `SSE` | 东南偏南风 | 157.5 | 146.25 - 168.75 |
| `S` | 南风 | 180 | 168.75 - 191.25 |
| `SSW` | 西南偏南风 | 202.5 | 191.25 - 213.75 |
| `SW` | 西南风 | 225 | 213.75 - 236.25 |
| `WSW` | 西南偏西风 | 247.5 | 236.25 - 258.75 |
| `W` | 西风 | 270 | 258.75 - 281.25 |
| `WNW` | 西北偏西风 | 292.5 | 281.25 - 303.75 |
| `NW` | 西北风 | 315 | 303.75 - 326.25 |
| `NNW` | 西北偏北风 | 337.5 | 326.25 - 348.75 |
| `Rotational` | 旋转风 | -999 | - |
| `None` | 无持续风向 | -1 | - |

## 风力等级[#](https://dev.qweather.com/docs/api/weather/wind-guide#wind-scale)

风力等级采用[蒲福风级](https://en.wikipedia.org/wiki/Beaufort_scale)（Beaufort scale或Beaufort wind force scale），根据风的强弱，将风力划分为0-12共13个等级。在1946年，蒲福风级扩展到17级，主要适用于热带气旋，中国台风网[在热带气旋中使用到了扩展级别](https://tcdata.typhoon.org.cn/zy_wind.html)。

风力等级与风速的经验方程如下：

```
V = 0.836 × B^(3/2)
```

其中`V`是海平面以上10米处的等效风速，`B`是蒲福级数。例如，B = 9.5 与 24.5 m/s 相关，等于“蒲福风级10级”的下限。

#### 标准蒲福风级0-12

| 蒲福风级 | 术语(ZH) | 术语(EN) | 风速 | 浪高 | 海上情况 | 陆上情况 |
| --- | --- | --- | --- | --- | --- | --- |
| 0 | 无风 | Calm | < 1 knot < 1 mph < 2 km/h < 0.5 m/s | 0 m | 平静 | 静,烟直上 |
| 1 | 软风 | Light air | 1–3 knots 1–3 mph 2–5 km/h 0.5–1.5 m/s | 0.1 m | 微波峰无飞沫 | 烟示风向 |
| 2 | 轻风 | Light breeze | 4–6 knots 4–7 mph 6–11 km/h 1.6–3.3 m/s | 0.2–0.5 m | 小波峰未破碎 | 感觉有风 |
| 3 | 微风 | Gentle breeze | 7–10 knots 8–12 mph 12–19 km/h 3.4–5.5 m/s | 0.6–1.0 m | 小波峰顶破裂 | 旌旗展开 |
| 4 | 和风 | Moderate breeze | 11–16 knots 13–18 mph 20–28 km/h 5.5–7.9 m/s | 1.0-1.5 m | 小浪白沫波峰 | 吹起尘土 |
| 5 | 清风 | Fresh breeze | 17–21 knots 19–24 mph 29–38 km/h 8–10.7 m/s | 2.0–2.5 m | 中浪折沫峰群 | 小树摇摆 |
| 6 | 强风 | Strong breeze | 22–27 knots 25–31 mph 39–49 km/h 10.8–13.8 m/s | 3.0–4.0 m | 大浪白沫离峰 | 电线有声 |
| 7 | 疾风 | Near gale | 28–33 knots 32–38 mph 50–61 km/h 13.9–17.1 m/s | 4.0–5.5 m | 破峰白沫成条 | 步行困难 |
| 8 | 大风 | Gale | 34–40 knots 39–46 mph 62–74 km/h 17.2–20.7 m/s | 5.5–7.5 m | 浪长高有浪花 | 折毁树枝 |
| 9 | 烈风 | Strong gale | 41–47 knots 47–54 mph 75–88 km/h 20.8–24.4 m/s | 7.0–10.0 m | 浪峰倒卷 | 小损房屋 |
| 10 | 狂风 | Storm | 48–55 knots 55–63 mph 89–102 km/h 24.5–28.4 m/s | 9.0–12.5 m | 海浪翻滚咆哮 | 拔起树木 |
| 11 | 暴风 | Violent storm | 56–63 knots 64–72 mph 103–117 km/h 28.5–32.6 m/s | 11.5–16.0 m | 波峰全呈飞沫 | 损毁重大 |
| 12 | 飓风 | Hurricane | ≥ 64 knots ≥ 73 mph ≥ 118 km/h ≥ 32.7 m/s | ≥ 14.0 m | 海浪滔天 | 摧毁极大 |

#### 扩展蒲福风级13-17

| 蒲福风级 | 风速 | 浪高 | 对应台风等级 |
| --- | --- | --- | --- |
| 13 | 72～80 knots 134-149 km/h 37.0-41.4 m/s | >14 m | 台风 TY |
| 14 | 81～89 knots 150-166 km/h 41.5-46.1 m/s | >14 m | 强台风 STY |
| 15 | 90～99 knots 167-183 km/h 46.2-50.9 m/s | >14 m | 强台风 STY |
| 16 | 100～108 knots 184-201 km/h 51.0-56.0 m/s | >14 m | 超强台风 SuperTY |
| 17 | 109～119 knots 202-220 km/h 56.1-61.2 m/s | >14 m | 超强台风 SuperTY |
| >17 | ≧120 knots ≥221 km/h ≥61.3 m/s | >14 m | 超强台风 SuperTY |

台风等级参考：[《热带气旋等级 GBT 19201-2006》](https://tcdata.typhoon.org.cn/data/doc/TC_std.pdf)

<!-- END_DOC_24 -->

---


<!-- START_DOC_25: features_service-and-data.md (URL: https://dev.qweather.com/docs/features/service-and-data) -->
Title: 服务和数据

URL Source: https://dev.qweather.com/docs/features/service-and-data

Published Time: Thu, 27 Aug 2026 02:08:36 GMT

Markdown Content:
和风天气开发服务基于先进的气象和人工智能算法，为广大开发者和企业提供了丰富、精准的天气数据服务，帮助你在应用中展示天气，或助力你的业务决策。这一切都可以通过我们提供的开发工具，轻松的集成。

## 服务[#](https://dev.qweather.com/docs/features/service-and-data#service)

和风天气开发服务提供下列开发服务：

*   API
*   iOS SDK
*   Android SDK

你可以通过上述服务在全球范围内，轻松快速的获取各类天气数据服务。

## 数据[#](https://dev.qweather.com/docs/features/service-and-data#data)

我们支持中国（包括港澳台）及全球200+国家或地区的气象数据服务，城市或地区总数量超过50万个。

### 天气类[#](https://dev.qweather.com/docs/features/service-and-data#weather)

| 数据项目 | 更新频率 | 时间颗粒度 | 时间范围 | 地理范围 |
| --- | --- | --- | --- | --- |
| 实况天气 | 15分钟 | 实时 | 实时 | 全球 |
| 逐天预报 | 8小时 | 逐天 | 1-30天 | 全球 |
| 逐小时预报 | 1小时 | 逐小时 | 1-168小时 | 全球 |
| 格点实况天气 | 20分钟 | 实时 | 实时 | 全球 |
| 格点逐天预报 | 6小时 | 逐天 | 1-7天 | 全球 |
| 格点逐小时预报 | 1小时 | 逐小时 | 1-72小时 | 全球 |
| 分钟级降水 | 5分钟 | 5分钟 | 1-2小时 | 中国 |

### 天气指数[#](https://dev.qweather.com/docs/features/service-and-data#weather-indices)

| 数据项目 | 更新频率 | 时间颗粒度 | 时间范围 | 地理范围 |
| --- | --- | --- | --- | --- |
| 洗车指数 | 1小时 | 逐天 | 1-3天 | 全球 |
| 穿衣指数 | 1小时 | 逐天 | 1-3天 | 全球 |
| 运动指数 | 1小时 | 逐天 | 1-3天 | 全球 |
| 紫外线指数 | 1小时 | 逐天 | 1-3天 | 全球 |
| 钓鱼指数 | 1小时 | 逐天 | 1-3天 | 全球 |
| 舒适度指数 | 1小时 | 逐天 | 1-3天 | 中国 |
| 感冒指数 | 1小时 | 逐天 | 1-3天 | 中国 |
| 旅游指数 | 1小时 | 逐天 | 1-3天 | 中国 |
| 空气污染扩散条件指数 | 1小时 | 逐天 | 1-3天 | 中国 |
| 空调开启指数 | 1小时 | 逐天 | 1-3天 | 中国 |
| 过敏指数 | 1小时 | 逐天 | 1-3天 | 中国 |
| 太阳镜指数 | 1小时 | 逐天 | 1-3天 | 中国 |
| 化妆指数 | 1小时 | 逐天 | 1-3天 | 中国 |
| 晾晒指数 | 1小时 | 逐天 | 1-3天 | 中国 |
| 交通指数 | 1小时 | 逐天 | 1-3天 | 中国 |
| 防晒指数 | 1小时 | 逐天 | 1-3天 | 中国 |

### 空气类[#](https://dev.qweather.com/docs/features/service-and-data#air-quality)

| 数据项目 | 更新频率 | 时间颗粒度 | 时间范围 | 地理范围 |
| --- | --- | --- | --- | --- |
| 空气质量实况 | 1小时 | 实时 | 实时 | 全球 |
| 空气质量每日预报 | 1小时 | 天 | 1-7天 | 全球 |
| 空气质量每小时预报 | 1小时 | 小时 | 1-72小时 | 全球 |

### 太阳辐照[#](https://dev.qweather.com/docs/features/service-and-data#solar-radiation)

| 数据项目 | 更新频率 | 时间颗粒度 | 时间范围 | 地理范围 |
| --- | --- | --- | --- | --- |
| 太阳辐照预报 | 1小时 | 15分钟 | 1-60小时 | 全球 |

### 预警类[#](https://dev.qweather.com/docs/features/service-and-data#warning)

| 数据项目 | 更新频率 | 时间颗粒度 | 时间范围 | 地理范围 |
| --- | --- | --- | --- | --- |
| 灾害预警 | 5分钟 | 实时 | 实时 | 全球 |

### 天文类[#](https://dev.qweather.com/docs/features/service-and-data#astronomy)

| 数据项目 | 更新频率 | 时间颗粒度 | 时间范围 | 地理范围 |
| --- | --- | --- | --- | --- |
| 日出日落时间 | 24小时 | 逐天 | 1-60天 | 全球 |
| 月升月落时间 | 24小时 | 逐天 | 1-60天 | 全球 |
| 月相 | 24小时 | 逐天、逐小时 | 1-60天 | 全球 |

### 海洋类[#](https://dev.qweather.com/docs/features/service-and-data#ocean)

| 数据项目 | 更新频率 | 时间颗粒度 | 时间范围 | 地理范围 |
| --- | --- | --- | --- | --- |
| 潮汐 | 24小时 | 逐天 | 1-10天 | 全球 |

### 热带气旋（台风）[#](https://dev.qweather.com/docs/features/service-and-data#tropical-cyclones)

| 数据项目 | 更新频率 | 时间颗粒度 | 时间范围 | 地理范围 |
| --- | --- | --- | --- | --- |
| 台风列表 | 1小时 | 逐天 | 365天 | 中国 |
| 台风预报 | 1小时 | 逐小时 | 1-72小时 | 中国 |
| 台风位置和路径 | 1小时 | 逐小时 | 台风生命周期 | 中国 |

<!-- END_DOC_25 -->

---


<!-- START_DOC_26: terms_tos.md (URL: https://dev.qweather.com/docs/terms/tos) -->
Title: 服务条款

URL Source: https://dev.qweather.com/docs/terms/tos

Markdown Content:
[和风天气开发者服务](https://dev.qweather.com/)

[控制台](https://id.qweather.com/#/login?redirect=https://console.qweather.com)

*   [首页](https://dev.qweather.com/)
*   [文档](https://dev.qweather.com/docs/)
*   [图标](https://icons.qweather.com/)
*   [展示](https://dev.qweather.com/showcase/)
*   [帮助](https://dev.qweather.com/help/)
*   [价格](https://dev.qweather.com/price/)
*   Search K 

*   [文档](https://dev.qweather.com/docs/)
*   >
*   [条款](https://dev.qweather.com/docs/terms/)
*   >
*   [服务条款](https://dev.qweather.com/docs/terms/tos/)

#### [开始使用](https://dev.qweather.com/docs/start/)

*   [特性](https://dev.qweather.com/docs/features/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [帐号管理](https://dev.qweather.com/docs/account/)

#### [开发文档](https://dev.qweather.com/docs/api/)

*   [GeoAPI](https://dev.qweather.com/docs/api/geoapi/)
*   [天气预报](https://dev.qweather.com/docs/api/weather/)
*   [分钟预报](https://dev.qweather.com/docs/api/minutely/)
*   [预警](https://dev.qweather.com/docs/api/warning/)
*   [天气指数](https://dev.qweather.com/docs/api/indices/)
*   [空气质量](https://dev.qweather.com/docs/api/air-quality/)
*   [时光机](https://dev.qweather.com/docs/api/time-machine/)
*   [热带气旋（台风）](https://dev.qweather.com/docs/api/tropical-cyclone/)
*   [海洋数据](https://dev.qweather.com/docs/api/ocean/)
*   [太阳辐射](https://dev.qweather.com/docs/api/solar-radiation/)
*   [天文](https://dev.qweather.com/docs/api/astronomy/)
*   [控制台API](https://dev.qweather.com/docs/api/console/)

#### [实用资料](https://dev.qweather.com/docs/resource/)

*   [专用词汇表](https://dev.qweather.com/docs/resource/glossary/)
*   [错误码](https://dev.qweather.com/docs/resource/error-code/)
*   [多语言](https://dev.qweather.com/docs/resource/language/)
*   [常见城市列表](https://dev.qweather.com/docs/resource/location-list/)
*   [元数据](https://dev.qweather.com/docs/resource/metadata/)
*   [单位](https://dev.qweather.com/docs/resource/unit/)

#### [最佳实践](https://dev.qweather.com/docs/best-practices/)

*   [不要假设](https://dev.qweather.com/docs/best-practices/no-assumptions/)
*   [优化请求](https://dev.qweather.com/docs/best-practices/optimize-requests/)
*   [处理Gzip](https://dev.qweather.com/docs/best-practices/gzip/)
*   [缓存你的数据](https://dev.qweather.com/docs/best-practices/cache/)
*   [安全指南](https://dev.qweather.com/docs/best-practices/security-guidelines/)

#### [财务与费用](https://dev.qweather.com/docs/finance/)

*   [计费方式和支付](https://dev.qweather.com/docs/finance/billing-and-payment/)
*   [按量计费定价](https://dev.qweather.com/docs/finance/pricing/)
*   [节省计划](https://dev.qweather.com/docs/finance/savings-plans/)
*   [增值税发票](https://dev.qweather.com/docs/finance/vat-invoice/)

#### [条款](https://dev.qweather.com/docs/terms/)

*   [使用限制](https://dev.qweather.com/docs/terms/restriction/)
*   [注明来源](https://dev.qweather.com/docs/terms/attribution/)
*   [服务条款](https://dev.qweather.com/docs/terms/tos/)

# 服务条款

使用和风天气开发平台，你需要了解并同意我们的开发者许可协议、隐私政策和免责声明。

*   [开发者许可协议](https://www.qweather.com/terms/developers-eula)
*   [隐私政策](https://www.qweather.com/terms/privacy)
*   [免责声明](https://www.qweather.com/terms/disclaimer)

### [和风天气开发者服务](https://dev.qweather.com/)

[联系](https://www.qweather.com/contact/)• [博客](https://blog.qweather.com/)• [GitHub](https://github.com/qwd)

Language 

### 文档

*   [开始使用](https://dev.qweather.com/docs/start/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [开发文档](https://dev.qweather.com/docs/api/)
*   [财务与费用](https://dev.qweather.com/docs/finance/)
*   [最佳实践](https://dev.qweather.com/docs/best-practices/)
*   [实用资料](https://dev.qweather.com/docs/resource/)

### 特性

*   [服务和数据](https://dev.qweather.com/docs/features/service-and-data/)
*   [全球部署](https://dev.qweather.com/docs/features/global-deployment/)
*   [性能](https://dev.qweather.com/docs/features/performance/)

### 更多

*   [天气预报](https://www.qweather.com/)
*   [天气地图](https://map.qweather.com/)
*   [下载APP](https://www.qweather.com/app/)

*   © 2026 [QWeather](https://www.qweather.com/en/)
*   [许可协议](https://www.qweather.com/terms/developers-eula/)
*   [隐私政策](https://www.qweather.com/terms/privacy)
*   [免责声明](https://www.qweather.com/terms/disclaimer/)

[京ICP备15048401号-11](https://beian.miit.gov.cn/)

<!-- END_DOC_26 -->

---


<!-- START_DOC_27: configuration_api-config.md (URL: https://dev.qweather.com/docs/configuration/api-config) -->
Title: 构建 API 请求

URL Source: https://dev.qweather.com/docs/configuration/api-config

Markdown Content:
了解如何发送一个API请求。

现在，我们假设你已经创建了[项目和凭据](https://dev.qweather.com/docs/configuration/project-and-key/)，准备开始创建一个完整的API请求吧。

## 请求URL[#](https://dev.qweather.com/docs/configuration/api-config#request-url)

通常来讲，一个完整的API请求URL由scheme，host，path，path parameters和query parameters组成：

```
https://abcxyz.qweatherapi.com/weather/v1/current/{lat}/{lon}?lang=en
\___/   \____________________/\_________________/\__________/\______/
scheme          host               path             path       query
                                                   params      params
```

*   **scheme:** 仅支持HTTPS协议。
*   **host:** 开发者的[API Host](https://dev.qweather.com/docs/configuration/api-host/)，请在[控制台-设置](https://console.qweather.com/setting)中查看。
*   **path:** API的请求路径（或称之为API端点、Endpoint）。
*   **path params:** 路径参数均为必选参数。
*   **query params:** 查询参数，包括必选和可选参数，多个查询参数使用`&`分割。

> **提示：** 你必须对参数中的[特殊字符](https://dev.qweather.com/docs/best-practices/optimize-requests/#special-characters)进行URL编码。

## 添加身份认证[#](https://dev.qweather.com/docs/configuration/api-config#add-authentication)

我们在上一步创建了一个API请求URL，现在需要为它添加身份认证以便服务器可以识别我们的身份。了解[身份认证](https://dev.qweather.com/docs/configuration/authentication/)。

#### JWT

在请求标头中添加如下内容：

```
Authorization: Bearer eyJhbGciOiAiRWREU0EiLCJraWQiOiAiQUJDRDEyMzQifQ.eyJpc3MiOiJBQkNEMTIzNCIsImlhdCI6MTcwMzkxMjQwMCwiZXhwIjoxNzAzOTEyOTQwfQ.MEQCIFGLmpmAEwuhB74mR04JWg_odEau6KYHYLRXs8Bp_miIAiBMU5O13vnv9ieEBSK71v4UULMI4K5T9El6bCxBkW4BdA
```

#### API KEY

在请求标头中添加如下内容：

```
X-QW-Api-Key: ABCD1234EFGH
```

## Gzip[#](https://dev.qweather.com/docs/configuration/api-config#gzip)

请注意，和风天气开发服务的API均使用Gzip进行了压缩，这将极大的减少网络流量，加快请求。因此，当你在开发过程中，需要对返回的数据进行解压。请参考[最佳实践-Gzip](https://dev.qweather.com/docs/best-practices/gzip/)。

## 构建完整的API请求[#](https://dev.qweather.com/docs/configuration/api-config#build-a-complete-api-request)

你可以用熟悉的开发语言构建最终的API请求，这里使用curl获取北京实时天气为例：

```
# 将下列占位符替换为你的实际值:
# abcxyz.qweatherapi.com → 你的 API Host
# 1234.ABCD.5678 → 你生成的 JWT
# ABCD1234EFGH → 你的 API KEY

# JWT 身份认证

curl --compressed \
-H 'Authorization: Bearer 1234.ABCD.5678' \
'https://abcxyz.qweatherapi.com/weather/v1/current/39.92/116.41'

# API KEY 身份认证

curl --compressed \
-H "X-QW-Api-Key: ABCD1234EFGH" \
'https://abcxyz.qweatherapi.com/weather/v1/current/39.92/116.41'
```

对于大部分开发者来说，构建一个API URL并非难事，但我们仍然强烈建议你阅读[最佳实践-优化请求](https://dev.qweather.com/docs/best-practices/optimize-requests/)文档，了解发送API请求的一些常见问题和经验。

<!-- END_DOC_27 -->

---


<!-- START_DOC_28: api_warning_alert-guide.md (URL: https://dev.qweather.com/docs/api/warning/alert-guide) -->
Title: 关于预警信息

URL Source: https://dev.qweather.com/docs/api/warning/alert-guide

Markdown Content:
预警信息是由政府、气象部门或其他授权机构针对即将发生、正在发生或可能造成影响的危险天气及相关灾害发布的官方信息。它通常包含预警事件、严重程度、影响区域、有效时间和防御建议，帮助公众及相关系统及时了解风险并采取必要的防范措施。

各个国家和地区的天气预警在事件分类、语言、有效时间、更新方式和影响区域等方面可能采用不同的规则。使用天气预警 API 时，不应将预警视为内容固定、仅对应某个行政区划的静态信息，而应根据预警的生命周期、时间字段和实际影响区域进行展示和处理。

本文介绍处理预警信息时需要了解的基本约定，并提供覆盖范围、预警事件以及更新和取消机制的相关说明。

## 支持的国家和地区[#](https://dev.qweather.com/docs/api/warning/alert-guide#supported-regions)

请参考[预警的覆盖范围](https://dev.qweather.com/docs/api/warning/alert-coverage/)。

## 预警事件[#](https://dev.qweather.com/docs/api/warning/alert-guide#alert-events)

请参考[预警事件列表](https://dev.qweather.com/docs/api/warning/alert-events/)。

## 多语言[#](https://dev.qweather.com/docs/api/warning/alert-guide#language)

预警信息不支持所有[多语言](https://dev.qweather.com/docs/resource/language/)，优先使用本地语言、英语或中文。一些情况下，返回的数据可能包括多种语言，或者与你指定的语言不相符。

## 失效的预警[#](https://dev.qweather.com/docs/api/warning/alert-guide#%e5%a4%b1%e6%95%88%e7%9a%84%e9%a2%84%e8%ad%a6)

开发者可以通过下列方式判断一条预警信息是否已经失效：

*   API 不再返回此预警信息
*   预警已经被其他预警信息更新
*   预警已经被其他预警信息取消
*   超过了过期时间

## 更新和取消预警[#](https://dev.qweather.com/docs/api/warning/alert-guide#update-and-cancel)

请参考[预警的变更](https://dev.qweather.com/docs/api/warning/alert-changes/)。

## 预警的时间[#](https://dev.qweather.com/docs/api/warning/alert-guide#temporal)

天气预警包含多个不同含义的时间字段，应根据各自的语义进行处理。

**发布时间**`issuedTime`

表示发布机构创建并发出这条预警信息的时间。发布时间描述的是**这条信息是什么时候发布的**，而不是天气过程什么时候开始，也不一定代表预警从这一时刻开始生效。

**生效时间**`effectiveTime`

表示**这条预警信息从什么时候开始适用**。生效时间需要与极端天气过程的实际开始时间区分，生效时间可能早于或晚于预警事件的开始时间。

**事件开始时间**`onsetTime`

表示**极端天气事件预计开始发生的时间**，它描述的是天气事件本身，而不是预警消息本身。事件开始时间通常是一个预计时间，天气系统的发展具有不确定性，实际天气过程可能稍早或稍晚于该时间发生。

**过期时间**`expireTime`

表示**这条预警信息预计失效的时间**，它描述的是预警信息的有效期限，而不是天气现象必然结束的精确时间。 请注意，**天气过程可能提前结束，也可能持续时间比最初预计更长**，关于预警信息性质的变更，请查看[预警的变更](https://dev.qweather.com/docs/api/warning/alert-changes/)。

> **时间没有先后顺序**，例如极端天气已经发生，此时预警信息的发布时间可能晚于预警事件的开始时间。

例如：

```
issuedTime:      10:00 
effectiveTime:   10:25
onsetTime:       14:00
expireTime:      20:00
```

_表示发布机构在 10:00 发布了预警信息，信息从 10:25 起生效，预计相关极端天气事件在 14:00 开始，本信息在 20:00 失效。_

## 影响区域[#](https://dev.qweather.com/docs/api/warning/alert-guide#affected-area)

极端天气受到天气系统移动、局地大气条件、地形以及其他自然因素影响，并不会按照人为划定的行政区划边界发生或停止。因此，预警信息应首先被理解为一个**地理覆盖范围**，而不是一个简单绑定到行政区划代码上的信息对象。

预警事件的影响区域一般表现为：

*   全部或部分行政区域
*   一片区域的描述
*   地理多边形或圆形区域
*   非行政区划的其他地理编码

对于开发者而言，无需判断影响区域的范围，只需要提交目标地点的经纬度，天气预警 API 即会返回适用于该位置的有效天气预警信息。

另一个需要注意的事情是，**发布预警的行政区域或行政机构，并不一定等同于天气实际影响区域。**

例如：

```
河北省气象台发布暴雨黄色预警信号：
预计11日白天到12日夜间，张家口、承德、保定、雄安新区、廊坊、石家庄、邢台、邯郸有较强降水，雨量分布不均，部分地区1小时雨量可达50毫米以上或6小时雨量可达70毫米以上，其中保定、石家庄西部、邢台南部、邯郸等地的局部地区累计降水量可达120-150毫米，个别地点可能超过200毫米；降雨时局地伴有雷电和短时大风（8-10级）等强对流天气。山区及浅山区有出现山洪、泥石流、滑坡等次生灾害的风险，低洼地区有积水风险，请注意防范。
```

那么：

```
保定市坐标   → 返回预警
承德市坐标   → 返回预警
唐山市坐标   → 不返回预警
沧州市坐标   → 不返回预警
```

<!-- END_DOC_28 -->

---


<!-- START_DOC_29: api_ocean.md (URL: https://dev.qweather.com/docs/api/ocean) -->
Title: 海洋数据

URL Source: https://dev.qweather.com/docs/api/ocean

Markdown Content:
[和风天气开发者服务](https://dev.qweather.com/)

[控制台](https://id.qweather.com/#/login?redirect=https://console.qweather.com)

*   [首页](https://dev.qweather.com/)
*   [文档](https://dev.qweather.com/docs/)
*   [图标](https://icons.qweather.com/)
*   [展示](https://dev.qweather.com/showcase/)
*   [帮助](https://dev.qweather.com/help/)
*   [价格](https://dev.qweather.com/price/)
*   Search K 

*   [文档](https://dev.qweather.com/docs/)
*   >
*   [开发文档](https://dev.qweather.com/docs/api/)
*   >
*   [海洋数据](https://dev.qweather.com/docs/api/ocean/)

#### [开始使用](https://dev.qweather.com/docs/start/)

*   [特性](https://dev.qweather.com/docs/features/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [帐号管理](https://dev.qweather.com/docs/account/)

#### [开发文档](https://dev.qweather.com/docs/api/)

*   [GeoAPI](https://dev.qweather.com/docs/api/geoapi/)
*   [天气预报](https://dev.qweather.com/docs/api/weather/)
*   [分钟预报](https://dev.qweather.com/docs/api/minutely/)
*   [预警](https://dev.qweather.com/docs/api/warning/)
*   [天气指数](https://dev.qweather.com/docs/api/indices/)
*   [空气质量](https://dev.qweather.com/docs/api/air-quality/)
*   [时光机](https://dev.qweather.com/docs/api/time-machine/)
*   [热带气旋（台风）](https://dev.qweather.com/docs/api/tropical-cyclone/)
*   [海洋数据](https://dev.qweather.com/docs/api/ocean/)

    *   [GET 潮汐](https://dev.qweather.com/docs/api/ocean/tide/)

*   [太阳辐射](https://dev.qweather.com/docs/api/solar-radiation/)
*   [天文](https://dev.qweather.com/docs/api/astronomy/)
*   [控制台API](https://dev.qweather.com/docs/api/console/)

#### [实用资料](https://dev.qweather.com/docs/resource/)

*   [专用词汇表](https://dev.qweather.com/docs/resource/glossary/)
*   [错误码](https://dev.qweather.com/docs/resource/error-code/)
*   [多语言](https://dev.qweather.com/docs/resource/language/)
*   [常见城市列表](https://dev.qweather.com/docs/resource/location-list/)
*   [元数据](https://dev.qweather.com/docs/resource/metadata/)
*   [单位](https://dev.qweather.com/docs/resource/unit/)

#### [最佳实践](https://dev.qweather.com/docs/best-practices/)

*   [不要假设](https://dev.qweather.com/docs/best-practices/no-assumptions/)
*   [优化请求](https://dev.qweather.com/docs/best-practices/optimize-requests/)
*   [处理Gzip](https://dev.qweather.com/docs/best-practices/gzip/)
*   [缓存你的数据](https://dev.qweather.com/docs/best-practices/cache/)
*   [安全指南](https://dev.qweather.com/docs/best-practices/security-guidelines/)

#### [财务与费用](https://dev.qweather.com/docs/finance/)

*   [计费方式和支付](https://dev.qweather.com/docs/finance/billing-and-payment/)
*   [按量计费定价](https://dev.qweather.com/docs/finance/pricing/)
*   [节省计划](https://dev.qweather.com/docs/finance/savings-plans/)
*   [增值税发票](https://dev.qweather.com/docs/finance/vat-invoice/)

#### [条款](https://dev.qweather.com/docs/terms/)

*   [使用限制](https://dev.qweather.com/docs/terms/restriction/)
*   [注明来源](https://dev.qweather.com/docs/terms/attribution/)
*   [服务条款](https://dev.qweather.com/docs/terms/tos/)

# 海洋数据

海洋数据 API 提供全球主要港口和城市的潮汐数据。

API

[GET 潮汐](https://dev.qweather.com/docs/api/ocean/tide/)
获取全球未来10天的满潮、干潮及逐小时潮汐数据。

### [和风天气开发者服务](https://dev.qweather.com/)

[联系](https://www.qweather.com/contact/)• [博客](https://blog.qweather.com/)• [GitHub](https://github.com/qwd)

Language 

### 文档

*   [开始使用](https://dev.qweather.com/docs/start/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [开发文档](https://dev.qweather.com/docs/api/)
*   [财务与费用](https://dev.qweather.com/docs/finance/)
*   [最佳实践](https://dev.qweather.com/docs/best-practices/)
*   [实用资料](https://dev.qweather.com/docs/resource/)

### 特性

*   [服务和数据](https://dev.qweather.com/docs/features/service-and-data/)
*   [全球部署](https://dev.qweather.com/docs/features/global-deployment/)
*   [性能](https://dev.qweather.com/docs/features/performance/)

### 更多

*   [天气预报](https://www.qweather.com/)
*   [天气地图](https://map.qweather.com/)
*   [下载APP](https://www.qweather.com/app/)

*   © 2026 [QWeather](https://www.qweather.com/en/)
*   [许可协议](https://www.qweather.com/terms/developers-eula/)
*   [隐私政策](https://www.qweather.com/terms/privacy)
*   [免责声明](https://www.qweather.com/terms/disclaimer/)

[京ICP备15048401号-11](https://beian.miit.gov.cn/)

<!-- END_DOC_29 -->

---


<!-- START_DOC_30: best-practices_cache.md (URL: https://dev.qweather.com/docs/best-practices/cache) -->
Title: 缓存你的数据

URL Source: https://dev.qweather.com/docs/best-practices/cache

Published Time: Thu, 27 Aug 2026 02:08:36 GMT

Markdown Content:
## 缓存你的数据

天气数据普遍具有时效性，对于不同的数据种类，时效性也有所不同。在你请求和风天气数据的时候，可以考虑设置弹性的、合理的缓存，这样可以有效降低服务器、带宽的消耗，加快用户的访问。

> **注意：** 缓存不是强制的要求，请认真阅读本文档，然后根据你的产品进行充分的评估，否则可能会对你的产品造成严重错误。**如果你不确定，或者无法处理所有应用场景，请不要使用缓存。**

> **警告：** 你不能缓存、提取、批量存储GeoAPI中提供的所有数据，这将违反我们的[开发者许可协议](https://www.qweather.com/terms/developers-eula)或侵犯版权，这些数据的版权所有者可能会对你采取法律行动。

## 合理性[#](https://dev.qweather.com/docs/best-practices/cache#reasonable)

对于时效性较强的数据，你不应该设置过长的缓存时间，例如实时天气，一天的温度变化较多，如果你设置超过了1小时的缓存，有可能造成用户在上午甚至中午查看到的是早上较为凉爽的气温。

> **提示：** 缓存数据与原数据的间隔时间最多相差2倍，例如你的缓存时间与和风天气的更新频率均为60分钟，和风天气在15:00发布了新的数据，用户从15:59开始访问你的产品，在16:59的时候用户通过你的缓存获取依然是15:00发布的原数据，在这种情况下，缓存数据与原数据的间隔时间达到了119分钟。

## 弹性[#](https://dev.qweather.com/docs/best-practices/cache#flexible)

缓存应该是弹性的，你不应该为所有数据或在所有时间范围内设置相同的缓存策略，否则你的程序可能出现严重错误或崩溃，因此你需要特别注意：

*   跨小时和跨天的场景，否则用户可能无法查看到完整的天气数据，严重情况可能导致你的程序错误。

> **例如：** 对于逐天预报，用户在23:00使用了你的产品查看了7天预报，在第二天00:00之后，如果你不刷新缓存，用户看到的7天预报的第一天将变成前一天或者只剩下6天预报，极端情况下，你的程序还可能报错。

*   夏令时对缓存的影响，避免夏令时开始和结束的时刻发生时间显示错误的问题。
*   如果你在服务端采用缓存，需要注意时区的问题，每个国家会有不同的时区，你设置的弹性缓存应该确保在不同时区都可以正常工作。

## 清除缓存[#](https://dev.qweather.com/docs/best-practices/cache#clear-cache)

你应该为你的程序设置一种可以立即清除缓存的功能，当出现错误的时候，可以通过清除缓存来实时的解决问题，而不需要重新发布新版本程序。

## 推荐的缓存时间[#](https://dev.qweather.com/docs/best-practices/cache#recommended-cache-time)

对于不同数据设置的缓存时间，我们推荐如下，当然更少的缓存时间可以获得更加及时的数据响应，还包括我们额外对数据进行的修复和优化。

> **注意：** 当已发布的数据需要进行修复时，我们会增加更新次数和更新频率。

> **注意：** 推荐的缓存时间并非适用于所有场景和需求，你应该根据你的产品进行评估。和风天气的数据发布频率也有可能会变更，我们会更新文档。

| 数据类型 | 推荐的缓存时间 | 和风天气的更新频率 |
| --- | --- | --- |
| 实时天气 | 10-30分钟 | 5-10分钟 |
| 逐小时天气预报 | 30-60分钟 | 60分钟 |
| 逐天天气预报 | 1-6小时 | 60分钟 |
| 天气预警 | 5-20分钟 | 3分钟 |
| 天气指数 | 6-12小时 | 60分钟 |
| 分钟降水 | 5-10分钟 | 5分钟 |
| 实时空气质量 | 30-60分钟 | 30-60分钟 |
| 空气质量逐天预报 | 8-12小时 | 1-6小时 |
| 潮汐 | 8-12小时 | 8小时 |
| 台风 | 活跃期 20分钟 非活跃期60分钟 | 10-60分钟 |
| 太阳辐照 | 6小时 | 3-6小时 |

## 限制[#](https://dev.qweather.com/docs/best-practices/cache#restrictions)

你不能缓存、提取、批量存储GeoAPI中提供的所有数据，这将违反我们的[使用限制](https://dev.qweather.com/docs/terms/restriction/)和[开发者许可协议](https://www.qweather.com/terms/developers-eula)。

GeoAPI中提供的数据内容来源于多个地理信息服务商，他们拥有不同的版权许可，大多数服务商（几乎是所有）许可你实时的使用，但禁止你将这些数据进行任何形式的存储，否则你可能会面临严重的法律风险。

<!-- END_DOC_30 -->

---


<!-- START_DOC_31: finance_billing-and-payment.md (URL: https://dev.qweather.com/docs/finance/billing-and-payment) -->
Title: 计费方式和支付

URL Source: https://dev.qweather.com/docs/finance/billing-and-payment

Markdown Content:
## 计费方式和支付

和风天气开发服务的计费方式采用按量计费订阅模式，是更加透明、简单和有竞争力的定价方案。本文档将介绍我们的计费和支付系统是如何工作的。

> **提示：** 本文档中关于价格计算的结果均为预估值，最终费用可能略有出入。

## 计费方式[#](https://dev.qweather.com/docs/finance/billing-and-payment#billing)

和风天气开发服务采用按量计费的后付费模式，即按照你的每一次请求记录费用，你不需要提前预付大量资金，也不需要为用不到的服务买单，你只需要为你实际使用的部分付费即可。另外，按量计费的定价是阶梯价格，意味着你使用的越多，单次请求的价格越低。

你的所有请求量和应计费用都将记录在账单中，我们将根据账单向你收取费用。以下是关于计费的重要规则：

*   **计价单位:** 按量计费的计价单位是一次请求。
*   **账单周期:** 按量计费的账单周期是一个自然月，在每个月的第一天生成。
*   **应计费用:** 应计费用是你每个小时产生的请求所需要支付的费用，一个自然月内所有小时的应计费用之和即为当月按量计费账单的金额。
*   **价格累进周期:** 按量计费的阶梯价格累进周期是一个自然月，即在每个月的最开始，都将按照阶梯价格的第一档开始计算。
*   **最小计费金额：** 0.01元。不足0.01元的按量计费项目以0.01元计费。

关于按量计费的定价请参考[按量计费定价](https://dev.qweather.com/docs/finance/pricing/)。

#### 举例说明

_**在一个月内请求了3万次天气API**_

下个月你需要支付 **0** 元

```
30,000次请求 x 0元 = 0
```

_**在一个月内请求了100万次天气API和100万次预警API**_

下个月你需要支付 **1,165** 元

```
50,000次请求 x 0元 + 950,000次请求 x 0.0007元 + 1,000,000次请求 x 0.0005元 = 1,165
```

_**在一个小时内请求了150次天气API（以Tier2价格为例）**_

这个小时的应计费用是 0.11 元（不足0.01元按0.01元计算）

```
150次请求 x 0.0007元 = 0.105元
```

### 月中账单[#](https://dev.qweather.com/docs/finance/billing-and-payment#mid-month-billing)

如果你在一个月内的应计费用超过了你的[信用额度](https://dev.qweather.com/docs/finance/billing-and-payment/#credit-limit)，你可能会收到月中账单。通常，你需要在48小时内（或根据支付日期）支付月中账单，否则你的服务可能被中断，参考[逾期支付](https://dev.qweather.com/docs/finance/billing-and-payment/#overdue)。

### 信用额度[#](https://dev.qweather.com/docs/finance/billing-and-payment#credit-limit)

所有开发者都将被授予一定信用额度，如果你的帐号可用额度 = 0，将消耗你的信用额度。在你支付完账单后，信用额度将恢复。信用额度会随着请求量、支付历史记录而动态的变化，一般来说，按时支付账单将逐步提高信用额度。

### 可用额度[#](https://dev.qweather.com/docs/finance/billing-and-payment#balance)

你支付/充值的资金将存入帐号可用额度，是你帐号中可以使用的资金，用于支付账单费用。当可用额度是负数的时候，代表你有一个或多个待支付的账单，你应该在应付日期前确保可用额度大于或等于0。

请注意，存入可用额度的资金不支持开具增值税发票，在使用可用额度支付账单后，可以根据账单金额申请发票。

### 如何降低成本[#](https://dev.qweather.com/docs/finance/billing-and-payment#how-to-reduce-costs)

按量计费可以根据你的需求灵活的增加或减少请求量，避免提前预付大量金额。当你的请求量较多或需求较稳定的时候，你可以使用节省计费，大幅降低你的成本。参考[节省计划](https://dev.qweather.com/docs/finance/savings-plans/)。

对于有大量请求量的用户（通常指的是日均50万次请求以上），请与我们的商务专家联系，以便提供更佳的解决方案。

## 支付账单[#](https://dev.qweather.com/docs/finance/billing-and-payment#payment)

在账单（包括月中账单或购买其他服务的账单）生成后，我们会自动尝试从你的账户可用额度中扣除费用。如果扣款失败，你需要在约定的付款日期之前手动支付账单。

我们会在两个时间点尝试使用你的可用额度支付账单：

*   账单日期：在账单生成时，我们将尝试使用你的可用额度支付账单.
*   付款日期：在账单支付日期的第二天，我们会再次尝试使用你的可用额度支付账单。

### 手动支付[#](https://dev.qweather.com/docs/finance/billing-and-payment#manual-payment)

你可以进行一次性手动付款，这将增加你的可用额度，用于支付账单或其他预付费服务。

#### 支付指定账单

1.   [前往控制台-账单](https://console.qweather.com/finance/billing/)
2.   选择待支付的账单，点击账单旁的“支付”按钮
3.   如果你的可用额度大于账单金额，你将收到一个提示，点击“支付”按钮，否则你将进入支付页面。
4.   在支付页面选择支付方式，勾选“我已了解”，点击“支付”。

你也可以在收到的邮件中根据提示进行支付。

#### 充值支付

1.   [前往控制台-支付](https://console.qweather.com/finance/payment/)
2.   输入充值金额。
3.   选择支付方式，勾选“我已了解”，点击“支付”。

充值的金额将优先补足负数可用额度，即自动支付已生效但未支付的账单（例如按量计费账单或月中账单），剩余部分将保存在你的可用额度。

对于还未生效的待支付账单（例如创建的节省计划账单），需要你在充值后再点击对应账单的支付按钮，根据提示使用可用额度进行支付。

每次充值的最小金额为0.01元，如果你的可用额度为负数，此时充值的最小金额必须可以让可用额度大于或等于0。

再次提示，充值到可用额度的正数金额不能直接申请增值税发票，需要使用可用额度支付账单后，根据账单金额申请发票。

### 节省计划支付[#](https://dev.qweather.com/docs/finance/billing-and-payment#saving-plans-payment)

如果你购买了[节省计划](https://dev.qweather.com/docs/finance/savings-plans/)，节省计划中的承诺金额将实时、自动的支付按量计费订阅产生的费用。如果节省计划到期或承诺金额消耗完，你需要在下个月初支付当月剩余费用（如有）。

请注意，节省计划的承诺金额无法支付在其生效前产生的按量计费订阅费用，也无法用于支付其它账单。

### 支付方式[#](https://dev.qweather.com/docs/finance/billing-and-payment#payment-methods)

我们提供多种支付方式。

**支付宝**

对于中国地区的用户，我们推荐使用支付宝进行支付。支持支付宝中绑定的信用卡、储蓄卡、余额以及花呗。

**银行转帐**

对于中国地区的企业开发者，我们支持使用银行转账进行支付：

*   请使用与你帐号企业名称相同的企业向我们发起转账，否则我们不会将转账金额添加到你的可用额度或在30天内退款。
*   和风天气的银行信息可以在[控制台-支付](https://console.qweather.com/finance/payment/)页面查看。
*   请在转账时**备注你的开发者ID**，以便我们能够快速的识别并防止金融欺诈，否则我们不会将转账金额添加到你的可用额度，此时**你需要提交工单并上传对应的银行流水单**。
*   银行转账以银行到账为准（如遇节假日可能会延后），无法做到实时支付。转账成功之后的1个工作日内（如遇法定节假日可能会顺延），我们会将转账金额添加到你的和风天气可用额度，你同时会收到一封邮件通知。

如果发起银行转账3个工作日后你的可用额度没有增加，请参考以下情况：

*   银行转账账号错误，请核对上述和风天气的银行帐号信息。
*   没有备注开发者ID，请登录控制台提交工单说明，并上传银行流水单。
*   个人银行帐号付款，我们将在10个工作日内原路退回转账金额。
*   发起转账的企业名称与你的账号开发者名称不一致，请登录控制台提交工单说明，可能需要你变更开发者信息或更改发起转账的企业。
*   当前是法定节假日或非工作时间。

### 逾期欠款[#](https://dev.qweather.com/docs/finance/billing-and-payment#overdue)

如果账单未能在应付日期前支付，则进入逾期/欠款状态，此时你的和风天气开发服务将被暂停，请尽快完成账单的支付。

> **警告：** 如“逾期欠款”状态持续30天，你的帐号将被冻结，并且你仍然需要支付账单。参考[帐号冻结](https://dev.qweather.com/docs/account/suspension/)。

## 退款[#](https://dev.qweather.com/docs/finance/billing-and-payment#refund)

你可以为节省计划和正数可用额度申请退款，其中节省计划必须还未生效且没有开具增值税发票。可用额度的退款将在次月按量计费订阅出账后完成。

所有退款均需要支付**5%交易手续费**，退款金额将退回到原支付渠道。如需退款，请[提交工单](https://console.qweather.com/support/ticket/new/)申请。

如果你的帐号被冻结或付款时间超过180天，则无法申请退款。

## 增值税[#](https://dev.qweather.com/docs/finance/billing-and-payment#value-added-tax)

对于中国大陆地区用户的购买，账单金额已经包含了增值税。如需要获取增值税发票，参考[增值税发票](https://dev.qweather.com/docs/finance/vat-invoice/)。

对于其他国家或地区用户的购买，账单金额不包含所需要征收的任何税种。

<!-- END_DOC_31 -->

---


<!-- START_DOC_32: api_air-quality_air-station.md (URL: https://dev.qweather.com/docs/api/air-quality/air-station) -->
Title: 监测站数据

URL Source: https://dev.qweather.com/docs/api/air-quality/air-station

Markdown Content:
## 监测站数据

监测站数据API提供各个国家或地区监测站的污染物浓度值。

> **警告：** 监测站的观测值仅供参考，可能由于故障、移除、维护或当地法律法规等各种原因导致数据延迟或缺失，我们无法确保该数据的可用性。

## 请求路径[#](https://dev.qweather.com/docs/api/air-quality/air-station#endpoint)

`GET /airquality/v1/stations/{locationId}`

## 参数[#](https://dev.qweather.com/docs/api/air-quality/air-station#parameters)

#### 路径参数

*   locationId 必选

string 
空气质量监测站的LocationID，LocationID可通过[GeoAPI](https://dev.qweather.com/docs/api/geoapi/)获取。例如 `P58911` 

#### 查询参数

*   lang

string 

## 请求示例[#](https://dev.qweather.com/docs/api/air-quality/air-station#request-example)

```
curl -X GET --compressed \
-H 'Authorization: Bearer your_token' \
'https://your-api-host/airquality/v1/stations/P58911'
```

请将`your_token`替换为你的[JWT身份认证](https://dev.qweather.com/docs/configuration/authentication/)，将`your_api_host`替换为你的[API Host](https://dev.qweather.com/docs/configuration/api-host/)

[试一下](https://dev.qweather.com/api-explore/#/Air%20Quality/getAirqualityAirStation)

## 返回数据[#](https://dev.qweather.com/docs/api/air-quality/air-station#response)

*   metadata

object 
    *   tag

string 
数据唯一标识 
    *   attributions

array 
数据归因信息或声明，必须与当前数据共同显示 

*   pollutants

array 
污染物列表 
    *   code

string 
污染物code，可选值：`pm10`、`pm2p5`、`co`、`no`、`no2`、`so2`、`o3`、`nmhc` 
    *   name

string 
污染物的名字 
    *   fullName

string 
污染物的全称 
    *   concentration

object 
污染物的浓度值 
        *   value

number 
数值 
        *   unit

string

<!-- END_DOC_32 -->

---


<!-- START_DOC_33: api_air-quality_health-effect-advice.md (URL: https://dev.qweather.com/docs/api/air-quality/health-effect-advice) -->
Title: 健康影响和建议

URL Source: https://dev.qweather.com/docs/api/air-quality/health-effect-advice

Markdown Content:
[和风天气开发者服务](https://dev.qweather.com/)

[控制台](https://id.qweather.com/#/login?redirect=https://console.qweather.com)

*   [首页](https://dev.qweather.com/)
*   [文档](https://dev.qweather.com/docs/)
*   [图标](https://icons.qweather.com/)
*   [展示](https://dev.qweather.com/showcase/)
*   [帮助](https://dev.qweather.com/help/)
*   [价格](https://dev.qweather.com/price/)
*    

*   [文档](https://dev.qweather.com/docs/)
*   >
*   [开发文档](https://dev.qweather.com/docs/api/)
*   >
*   [空气质量](https://dev.qweather.com/docs/api/air-quality/)
*   >
*   [健康影响和建议](https://dev.qweather.com/docs/api/air-quality/health-effect-advice/)

#### [开始使用](https://dev.qweather.com/docs/start/)

*   [特性](https://dev.qweather.com/docs/features/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [帐号管理](https://dev.qweather.com/docs/account/)

#### [开发文档](https://dev.qweather.com/docs/api/)

*   [GeoAPI](https://dev.qweather.com/docs/api/geoapi/)
*   [天气预报](https://dev.qweather.com/docs/api/weather/)
*   [分钟预报](https://dev.qweather.com/docs/api/minutely/)
*   [预警](https://dev.qweather.com/docs/api/warning/)
*   [天气指数](https://dev.qweather.com/docs/api/indices/)
*   [空气质量](https://dev.qweather.com/docs/api/air-quality/)

    *   [GET 实时空气质量](https://dev.qweather.com/docs/api/air-quality/air-current/)
    *   [GET 空气质量小时预报](https://dev.qweather.com/docs/api/air-quality/air-hourly-forecast/)
    *   [GET 空气质量每日预报](https://dev.qweather.com/docs/api/air-quality/air-daily-forecast/)
    *   [支持的空气质量指数](https://dev.qweather.com/docs/api/air-quality/aqi-list/)
    *   [污染物列表](https://dev.qweather.com/docs/api/air-quality/pollutant-list/)
    *   [空气质量覆盖范围](https://dev.qweather.com/docs/api/air-quality/aqi-coverage/)
    *   [健康影响和建议](https://dev.qweather.com/docs/api/air-quality/health-effect-advice/)
    *   [中国空气质量说明](https://dev.qweather.com/docs/api/air-quality/china-aqi/)

*   [时光机](https://dev.qweather.com/docs/api/time-machine/)
*   [热带气旋（台风）](https://dev.qweather.com/docs/api/tropical-cyclone/)
*   [海洋数据](https://dev.qweather.com/docs/api/ocean/)
*   [太阳辐射](https://dev.qweather.com/docs/api/solar-radiation/)
*   [天文](https://dev.qweather.com/docs/api/astronomy/)
*   [控制台API](https://dev.qweather.com/docs/api/console/)

#### [实用资料](https://dev.qweather.com/docs/resource/)

*   [专用词汇表](https://dev.qweather.com/docs/resource/glossary/)
*   [错误码](https://dev.qweather.com/docs/resource/error-code/)
*   [多语言](https://dev.qweather.com/docs/resource/language/)
*   [常见城市列表](https://dev.qweather.com/docs/resource/location-list/)
*   [元数据](https://dev.qweather.com/docs/resource/metadata/)
*   [单位](https://dev.qweather.com/docs/resource/unit/)

#### [最佳实践](https://dev.qweather.com/docs/best-practices/)

*   [不要假设](https://dev.qweather.com/docs/best-practices/no-assumptions/)
*   [优化请求](https://dev.qweather.com/docs/best-practices/optimize-requests/)
*   [处理Gzip](https://dev.qweather.com/docs/best-practices/gzip/)
*   [缓存你的数据](https://dev.qweather.com/docs/best-practices/cache/)
*   [安全指南](https://dev.qweather.com/docs/best-practices/security-guidelines/)

#### [财务与费用](https://dev.qweather.com/docs/finance/)

*   [计费方式和支付](https://dev.qweather.com/docs/finance/billing-and-payment/)
*   [按量计费定价](https://dev.qweather.com/docs/finance/pricing/)
*   [节省计划](https://dev.qweather.com/docs/finance/savings-plans/)
*   [增值税发票](https://dev.qweather.com/docs/finance/vat-invoice/)

#### [条款](https://dev.qweather.com/docs/terms/)

*   [使用限制](https://dev.qweather.com/docs/terms/restriction/)
*   [注明来源](https://dev.qweather.com/docs/terms/attribution/)
*   [服务条款](https://dev.qweather.com/docs/terms/tos/)

# 健康影响和建议

> **警告：** 健康影响和建议并非规范建议，也不具备法律效力，你应该了解或向你的用户告知：在任何时候，如有身体不适者应立即就医并遵医嘱。

空气质量的好坏对人类健康产生直接的影响。空气质量 API 提供了健康影响和建议的信息，对于大多数人来说，健康指导建议可以有效的指导他们的行动，尽可能的在发生空气污染时进行及时响应。这些建议区分了健康人群和敏感人群，其中敏感人群包括：

*   老人
*   孕妇
*   儿童
*   心脏疾病患者
*   呼吸系统疾病患者
*   长期的户外工作者
*   其他对空气异常敏感的人群

健康影响和建议不适用于所有国家和地区。

### [和风天气开发者服务](https://dev.qweather.com/)

[联系](https://www.qweather.com/contact/)• [博客](https://blog.qweather.com/)• [GitHub](https://github.com/qwd)

Language 

### 文档

*   [开始使用](https://dev.qweather.com/docs/start/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [开发文档](https://dev.qweather.com/docs/api/)
*   [财务与费用](https://dev.qweather.com/docs/finance/)
*   [最佳实践](https://dev.qweather.com/docs/best-practices/)
*   [实用资料](https://dev.qweather.com/docs/resource/)

### 特性

*   [服务和数据](https://dev.qweather.com/docs/features/service-and-data/)
*   [全球部署](https://dev.qweather.com/docs/features/global-deployment/)
*   [性能](https://dev.qweather.com/docs/features/performance/)

### 更多

*   [天气预报](https://www.qweather.com/)
*   [天气地图](https://map.qweather.com/)
*   [下载APP](https://www.qweather.com/app/)

*   © 2026 [QWeather](https://www.qweather.com/en/)
*   [许可协议](https://www.qweather.com/terms/developers-eula/)
*   [隐私政策](https://www.qweather.com/terms/privacy)
*   [免责声明](https://www.qweather.com/terms/disclaimer/)

[京ICP备15048401号-11](https://beian.miit.gov.cn/)

<!-- END_DOC_33 -->

---


<!-- START_DOC_34: finance_savings-plans.md (URL: https://dev.qweather.com/docs/finance/savings-plans) -->
Title: 节省计划

URL Source: https://dev.qweather.com/docs/finance/savings-plans

Published Time: Thu, 27 Aug 2026 02:08:36 GMT

Markdown Content:
节省计划用于抵扣请求API产生的费用，是针对数据请求量较多和较稳定的用户所推出的一项折扣计划，它不依赖于具体的数据，只需要用户承诺每年的消费金额就可以获取大量折扣。相比较标准按量计费，节省计划可以节省30%的成本。

_你可以理解为节省计划是办了一张VIP卡，使用这张卡的所有消费都会享受折扣价格。_

用户可以自行预估每天或每月的数据请求量，根据这些预估数据计算每年的大致费用，然后购买对应的节省计划。

> **例如：** 用户承诺每年消费1000元，并预付了1年的费用，此时该用户的API请求费用以按量计费价格的7折计算，即请求一次实时天气数据的价格从0.0007元降低到0.00049元。或者可以理解为，用户支付1000元，在未购买节省计划的情况下，可以请求实时天气约160万次；在购买节省计划后，可以请求实时天气约250万次。

## 承诺金额[#](https://dev.qweather.com/docs/finance/savings-plans#commitment-amount)

承诺金额是你承诺在一年内使用的金额，该金额将在1年内有效或消耗完毕后失效，参考[有效期](https://dev.qweather.com/docs/finance/savings-plans/#expiration)，同时在有效期内，所有API请求的费用均按照节省计划系数计算折扣。

承诺金额将自动支付按量计费订阅产生的费用。

## 承诺期数[#](https://dev.qweather.com/docs/finance/savings-plans#commitment-terms)

承诺期数是你购买节省计划需要预付的总时长。承诺期数目前最长为1年期。

## 节省系数[#](https://dev.qweather.com/docs/finance/savings-plans#savings-rate)

节省系数是你在使用节省计划期间按量计费价格所享受的折扣。

> **例如：** 购买1年期节省计划后，实时天气的单次请求费用为： 0.001元 x 0.7节省系数 = 0.0007元

| 承诺期数 | 节省系数 |
| --- | --- |
| 1 年 | 0.7 |

## 购买[#](https://dev.qweather.com/docs/finance/savings-plans#purchase)

1.   [前往控制台-节省计划](https://console.qweather.com/savings-plans/)
2.   输入承诺的金额
3.   选择[生效时间](https://dev.qweather.com/docs/finance/savings-plans/#effective)
4.   点击“下一步”
5.   确认购买信息，点击“创建”按钮

创建完成后，请尽快进行支付，超过支付日期的，节省计划将被取消。

在支付前，你可以随时取消已创建但未支付的节省计划。

## 价格测算[#](https://dev.qweather.com/docs/finance/savings-plans#price-calculator)

使用[价格计算器](https://console.qweather.com/price-calculator)可以快速的帮你计算所需要购买的节省计划。

## 有效期[#](https://dev.qweather.com/docs/finance/savings-plans#expiration)

节省计划中的承诺金额有效期为1年或在1年内消耗完毕。在节省计划到期后，如还有未使用的金额将作废处理，请参考下方的例子：

前提：在2022年3月20日购买了一份节省计划（1年期）

| 1年内使用完 | 1年内未使用完 |
| --- | --- |
| 在2022年12月30日23点，全部承诺金额使用完，从下一个小时开始的请求将不再享受节省计划折扣。 | 在2023年3月19日23点59分59秒时，节省计划内还有剩余承诺金额234元，则从2023年3月20日起，剩余的234元将作废无法再使用。 |

## 生效[#](https://dev.qweather.com/docs/finance/savings-plans#effective)

购买节省计划时，可以选择生效时间：

**支付后立即生效**

当你创建节省计划成功后，最多有24小时的时间进行付款，付款后节省计划立即生效。

**指定时间**

当你创建节省计划成功并完成支付后，节省计划将在你指定的时间生效。

再次提示，如果未能在支付日期前付款的节省计划将自动取消。

## 多份节省计划[#](https://dev.qweather.com/docs/finance/savings-plans#multiple-saving-plans)

你可以购买多份节省计划，可以叠加使用，以满足更加灵活的使用。多份节省计划，均按照购买时间先后顺序进行扣减。

<!-- END_DOC_34 -->

---


<!-- START_DOC_35: configuration.md (URL: https://dev.qweather.com/docs/configuration) -->
Title: 开发配置

URL Source: https://dev.qweather.com/docs/configuration

Markdown Content:
[和风天气开发者服务](https://dev.qweather.com/)

[控制台](https://id.qweather.com/#/login?redirect=https://console.qweather.com)

*   [首页](https://dev.qweather.com/)
*   [文档](https://dev.qweather.com/docs/)
*   [图标](https://icons.qweather.com/)
*   [展示](https://dev.qweather.com/showcase/)
*   [帮助](https://dev.qweather.com/help/)
*   [价格](https://dev.qweather.com/price/)
*   Search K 

*   [文档](https://dev.qweather.com/docs/)
*   >
*   [开发配置](https://dev.qweather.com/docs/configuration/)

#### [开始使用](https://dev.qweather.com/docs/start/)

*   [特性](https://dev.qweather.com/docs/features/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)

    *   [项目和凭据](https://dev.qweather.com/docs/configuration/project-and-key/)
    *   [API Host](https://dev.qweather.com/docs/configuration/api-host/)
    *   [身份认证](https://dev.qweather.com/docs/configuration/authentication/)
    *   [构建 API 请求](https://dev.qweather.com/docs/configuration/api-config/)
    *   [SDK 配置](https://dev.qweather.com/docs/configuration/sdk-config/)

*   [帐号管理](https://dev.qweather.com/docs/account/)

#### [开发文档](https://dev.qweather.com/docs/api/)

*   [GeoAPI](https://dev.qweather.com/docs/api/geoapi/)
*   [天气预报](https://dev.qweather.com/docs/api/weather/)
*   [分钟预报](https://dev.qweather.com/docs/api/minutely/)
*   [预警](https://dev.qweather.com/docs/api/warning/)
*   [天气指数](https://dev.qweather.com/docs/api/indices/)
*   [空气质量](https://dev.qweather.com/docs/api/air-quality/)
*   [时光机](https://dev.qweather.com/docs/api/time-machine/)
*   [热带气旋（台风）](https://dev.qweather.com/docs/api/tropical-cyclone/)
*   [海洋数据](https://dev.qweather.com/docs/api/ocean/)
*   [太阳辐射](https://dev.qweather.com/docs/api/solar-radiation/)
*   [天文](https://dev.qweather.com/docs/api/astronomy/)
*   [控制台API](https://dev.qweather.com/docs/api/console/)

#### [实用资料](https://dev.qweather.com/docs/resource/)

*   [专用词汇表](https://dev.qweather.com/docs/resource/glossary/)
*   [错误码](https://dev.qweather.com/docs/resource/error-code/)
*   [多语言](https://dev.qweather.com/docs/resource/language/)
*   [常见城市列表](https://dev.qweather.com/docs/resource/location-list/)
*   [元数据](https://dev.qweather.com/docs/resource/metadata/)
*   [单位](https://dev.qweather.com/docs/resource/unit/)

#### [最佳实践](https://dev.qweather.com/docs/best-practices/)

*   [不要假设](https://dev.qweather.com/docs/best-practices/no-assumptions/)
*   [优化请求](https://dev.qweather.com/docs/best-practices/optimize-requests/)
*   [处理Gzip](https://dev.qweather.com/docs/best-practices/gzip/)
*   [缓存你的数据](https://dev.qweather.com/docs/best-practices/cache/)
*   [安全指南](https://dev.qweather.com/docs/best-practices/security-guidelines/)

#### [财务与费用](https://dev.qweather.com/docs/finance/)

*   [计费方式和支付](https://dev.qweather.com/docs/finance/billing-and-payment/)
*   [按量计费定价](https://dev.qweather.com/docs/finance/pricing/)
*   [节省计划](https://dev.qweather.com/docs/finance/savings-plans/)
*   [增值税发票](https://dev.qweather.com/docs/finance/vat-invoice/)

#### [条款](https://dev.qweather.com/docs/terms/)

*   [使用限制](https://dev.qweather.com/docs/terms/restriction/)
*   [注明来源](https://dev.qweather.com/docs/terms/attribution/)
*   [服务条款](https://dev.qweather.com/docs/terms/tos/)

# 开发配置

在开始使用和风天气开发服务之前，你需要进行一些基础配置，例如创建项目和KEY，下载SDK等等。

[项目和凭据](https://dev.qweather.com/docs/configuration/project-and-key/)
了解如何创建和管理项目与凭据。

[API Host](https://dev.qweather.com/docs/configuration/api-host/)
了解如何查看和使用开发者专属的 API Host，以安全地访问和风天气 API。

[身份认证](https://dev.qweather.com/docs/configuration/authentication/)
了解如何通过 JWT 或 API KEY 对和风天气开发服务的请求进行身份认证。

[构建 API 请求](https://dev.qweather.com/docs/configuration/api-config/)
了解和风天气 API 的请求地址、身份认证和压缩方式，并构建一个完整的 API 请求。

[SDK 配置](https://dev.qweather.com/docs/configuration/sdk-config/)
了解如何安装和配置和风天气 iOS SDK 和 Android SDK。

### [和风天气开发者服务](https://dev.qweather.com/)

[联系](https://www.qweather.com/contact/)• [博客](https://blog.qweather.com/)• [GitHub](https://github.com/qwd)

Language 

### 文档

*   [开始使用](https://dev.qweather.com/docs/start/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [开发文档](https://dev.qweather.com/docs/api/)
*   [财务与费用](https://dev.qweather.com/docs/finance/)
*   [最佳实践](https://dev.qweather.com/docs/best-practices/)
*   [实用资料](https://dev.qweather.com/docs/resource/)

### 特性

*   [服务和数据](https://dev.qweather.com/docs/features/service-and-data/)
*   [全球部署](https://dev.qweather.com/docs/features/global-deployment/)
*   [性能](https://dev.qweather.com/docs/features/performance/)

### 更多

*   [天气预报](https://www.qweather.com/)
*   [天气地图](https://map.qweather.com/)
*   [下载APP](https://www.qweather.com/app/)

*   © 2026 [QWeather](https://www.qweather.com/en/)
*   [许可协议](https://www.qweather.com/terms/developers-eula/)
*   [隐私政策](https://www.qweather.com/terms/privacy)
*   [免责声明](https://www.qweather.com/terms/disclaimer/)

[京ICP备15048401号-11](https://beian.miit.gov.cn/)

<!-- END_DOC_35 -->

---


<!-- START_DOC_36: api.md (URL: https://dev.qweather.com/docs/api) -->
Title: 开发文档

URL Source: https://dev.qweather.com/docs/api

Markdown Content:
和风天气开发服务提供了API、iOS SDK和Android SDK用以访问基于位置的天气数据，包括实况天气、30天预报、逐小时预报、空气质量AQI，灾害预警、分钟级降水、生活指数等天气数据服务。

_如需过时API/SDK的开发文档请访问[已弃用](https://dev.qweather.com/docs/deprecated/)。_

[GeoAPI](https://dev.qweather.com/docs/api/geoapi/)

和风天气 GeoAPI 提供全球地理信息、城市搜索、坐标反查等功能。

[天气预报](https://dev.qweather.com/docs/api/weather/)

和风天气预报 API 基于多模式融合、站点同化和人工智能算法，支持未来最多30天预报和分钟级实况数据，1公里分辨率，覆盖全球任意地点。

[分钟预报](https://dev.qweather.com/docs/api/minutely/)

分钟级降水API（临近预报）支持中国1公里精度的分钟级降雨预报数据，为每一分钟的降雨进行精准预测。

[预警](https://dev.qweather.com/docs/api/warning/)

和风天气预警 API 提供了全球官方发布的极端天气预警服务，覆盖全球大部分国家或地区。

[天气指数](https://dev.qweather.com/docs/api/indices/)

天气生活指数是一种根据多种气象要素而计算出的指数，使用不同类型的指数可以直观的了解天气对人类活动的影响。

[空气质量](https://dev.qweather.com/docs/api/air-quality/)

空气质量 API 提供指定位置的实时空气质量和预报数据、污染物和健康建议。支持100多个国家或地区的空气质量标准，1公里分辨率。

[热带气旋（台风）](https://dev.qweather.com/docs/api/tropical-cyclone/)

热带气旋 API 提供中国地区的台风信息，包括台风实时位置、路径和预报数据。

[海洋数据](https://dev.qweather.com/docs/api/ocean/)

海洋数据 API 提供全球主要港口和城市的潮汐数据。

[太阳辐射](https://dev.qweather.com/docs/api/solar-radiation/)

太阳辐射 API 提供全球辐射数据，包括DNI、DHI、GHI以及相关气象数据，15分钟间隔，1公里分辨率。

[天文](https://dev.qweather.com/docs/api/astronomy/)

天文API提供了全球任意地点未来60天的日出日落、太阳高度角、月升月落和月相数据，

[控制台API](https://dev.qweather.com/docs/api/console/)

控制台 API 提供近实时的财务和请求量数据，你可以根据这些数据评估使用率或者建立财务预警。

<!-- END_DOC_36 -->

---


<!-- START_DOC_37: account_developers.md (URL: https://dev.qweather.com/docs/account/developers) -->
Title: 开发者类型

URL Source: https://dev.qweather.com/docs/account/developers

Markdown Content:
当你首次使用开发者服务时，你将被要求选择开发者类型，一旦选定，目前暂不支持更改开发者类型。不同开发者类型在数据、性能上没有任何不同。

## 个人开发者[#](https://dev.qweather.com/docs/account/developers#individual)

如果你是独立的个人、学生或不需要额外企业功能的实体，可以选择个人开发者。

## 企业开发者[#](https://dev.qweather.com/docs/account/developers#organization)

如果你是位于中国的企业或组织实体，你可以注册成为企业开发者以便获取更多企业支持和服务，注册企业开发者需要1-3工作日的审核。

#### 变更企业信息

变更企业名称或统一社会信用代码，请参考下列步骤：

1.   [前往控制台设置](https://console.qweather.com/setting)
2.   点击企业名称旁的“编辑”。
3.   在申请变更企业信息区域点击“下一步”。
4.   填写申请人联系信息，选择变更类型。
5.   如果是变更为另一家企业，填写新企业的名称和统一社会信用代码，上传新企业的营业执照照片。点击“下一步”，下载变更申请表，打印、盖章、扫描并上传，提交审核。
6.   如果是仅更名，填写新的企业名称，上传新名称的企业的营业执照照片以及变更证明。点击“下一步”，再次确认变更信息，提交审核。

在填写变更信息的步骤中，你可以随时暂停，之后通过点击企业名称旁边的“编辑”再次继续填写。变更信息的审核需要3-5个工作日，审核通过后你将收到邮件通知，并可查看更新纪录。

变更其他企业信息，例如联系方式、注册地址或电话等，请前往[账单联系信息](https://console.qweather.com/finance/contact/)或[增值税发票信息](https://console.qweather.com/finance/vat-invoice/info/)自行修改。

## 对比[#](https://dev.qweather.com/docs/account/developers#comparisons)

| 功能 | 个人开发者 | 企业开发者 |
| --- | --- | --- |
| 付款方式 | 在线支付 | 在线支付 对公转账 |
| 增值税发票 | 增值税电子普通发票 | 增值税专用发票 增值税电子普通发票 |

<!-- END_DOC_37 -->

---


<!-- START_DOC_38: start.md (URL: https://dev.qweather.com/docs/start) -->
Title: 开始使用

URL Source: https://dev.qweather.com/docs/start

Published Time: Thu, 27 Aug 2026 02:08:36 GMT

Markdown Content:
欢迎使用和风天气开发服务，本篇文档将介绍一些基本概念和文档索引，便于你快速开始以及了解和风天气开发服务是如何运行的。

## 帐号[#](https://dev.qweather.com/docs/start#account)

你需要[注册一个帐号](https://id.qweather.com/#/register?redirect=https%3A%2F%2Fconsole.qweather.com)，用于管理你的项目、凭据、财务等事宜。请参考[帐号管理](https://dev.qweather.com/docs/account/)。

## 配置 API[#](https://dev.qweather.com/docs/start#configure-api)

在请求数据之前，需要一些准备工作：

*   创建[项目和凭据](https://dev.qweather.com/docs/configuration/project-and-key/)用来管理你的API
*   了解[身份认证](https://dev.qweather.com/docs/configuration/authentication/)，和风天气支持两种身份认证方式：**JSON Web Token (JWT)** 以及 **API KEY**
*   获取[API Host](https://dev.qweather.com/docs/configuration/api-host/)，这是你独立的API请求地址
*   了解如何[构建 API 请求](https://dev.qweather.com/docs/configuration/api-config/)

## API 调试[#](https://dev.qweather.com/docs/start#api-%e8%b0%83%e8%af%95)

我们提供了基于 Swagger UI 的 [API 调试工具](https://dev.qweather.com/api-explore/)，复制粘贴你的 API Host 和凭据，然后开始快速调试。

我们也建议你在编码前阅读每项 [API 的开发文档](https://dev.qweather.com/docs/api/)，同时包括[实用资料](https://dev.qweather.com/docs/resource/)和[最佳实践](https://dev.qweather.com/docs/best-practices/)，可以帮助你更高效的完成工作。

## 费用[#](https://dev.qweather.com/docs/start#finance)

和风天气的计费方式为按量计费，并采用阶梯价，意味着你的请求量越多，单价越低，并且当你停止使用时，你不需要支付任何费用。请参考[定价](https://dev.qweather.com/docs/finance/pricing/)和[计费方式和支付](https://dev.qweather.com/docs/finance/billing-and-payment/)。

如果你的请求量较多，你也可以使用节省计划来大幅降低你的成本，了解[节省计划](https://dev.qweather.com/docs/finance/savings-plans/)。

对于中国大陆地区的用户，我们的价格已经包含增值税，如需要开具增值税发票请参考[增值税发票](https://dev.qweather.com/docs/finance/vat-invoice/)。

## 帮助和支持[#](https://dev.qweather.com/docs/start#help-and-support)

你可以点击网页顶部的放大镜按钮去搜索需要的资料，或查看[常见问题](https://dev.qweather.com/help/)。99%的疑问可以在文档中找到答案。

你也可以提交工单，我们的技术专家将尽快帮助你。

[前往控制台 - 工单](https://console.qweather.com/support/ticket)

## 条款[#](https://dev.qweather.com/docs/start#terms)

为了提供一个更加公平、高效、合规的开发环境，你需要同意我们的各项服务条款，并在许可证允许的范围内使用和风天气开发服务。参考[条款](https://dev.qweather.com/docs/terms/)。

<!-- END_DOC_38 -->

---


<!-- START_DOC_39: api_air-quality.md (URL: https://dev.qweather.com/docs/api/air-quality) -->
Title: 空气质量

URL Source: https://dev.qweather.com/docs/api/air-quality

Markdown Content:
## 空气质量

空气质量 API 提供指定位置的实时空气质量和预报数据、污染物和健康建议。支持100多个国家或地区的空气质量标准，1公里分辨率。

API

[GET 实时空气质量](https://dev.qweather.com/docs/api/air-quality/air-current/)

获取指定地点1公里分辨率的实时空气质量、污染物和健康建议数据。

[GET 空气质量小时预报](https://dev.qweather.com/docs/api/air-quality/air-hourly-forecast/)

获取指定地点未来24小时的逐小时空气质量预报、污染物和健康建议数据。

[GET 空气质量每日预报](https://dev.qweather.com/docs/api/air-quality/air-daily-forecast/)

获取指定地点未来3天的每日空气质量预报、污染物和健康建议数据。

参考资料

[支持的空气质量指数](https://dev.qweather.com/docs/api/air-quality/aqi-list/)

和风空气质量 API 支持的空气质量指数和标准。

[污染物列表](https://dev.qweather.com/docs/api/air-quality/pollutant-list/)

和风空气质量 API 支持的污染物列表。

[空气质量覆盖范围](https://dev.qweather.com/docs/api/air-quality/aqi-coverage/)

和风空气质量 API 支持的国家和地区列表

[健康影响和建议](https://dev.qweather.com/docs/api/air-quality/health-effect-advice/)

针对当前空气质量的健康影响和健康指南。

[中国空气质量说明](https://dev.qweather.com/docs/api/air-quality/china-aqi/)

中国空气质量指数的说明和规范

<!-- END_DOC_39 -->

---


<!-- START_DOC_40: api_air-quality_aqi-coverage.md (URL: https://dev.qweather.com/docs/api/air-quality/aqi-coverage) -->
Title: 空气质量覆盖范围

URL Source: https://dev.qweather.com/docs/api/air-quality/aqi-coverage

Markdown Content:
和风空气质量 API 已经支持覆盖全球，其中 [QAQI](https://dev.qweather.com/docs/api/air-quality/aqi-list/#qaqi) 支持全球任意地点，[本地 AQI](https://dev.qweather.com/docs/api/air-quality/aqi-list/#local-aqi) 支持下列国家和地区：

| ISO 3166-1 | 国家或地区 | 支持的 AQIs |
| --- | --- | --- |
| ad | 安道尔 | `eu-eea` |
| be | 比利时 | `eu-eea` |
| bg | 保加利亚 | `eu-eea` |
| ca | 加拿大 | `ca-eccc` |
| cn | 中国 | `cn-mee``cn-mee-1h` |
| hr | 克罗地亚 | `eu-eea` |
| cz | 捷克 | `eu-eea` |
| dk | 丹麦 | `eu-eea` |
| fi | 芬兰 | `eu-eea` |
| fr | 法国 | `fr-atmo``eu-eea` |
| de | 德国 | `eu-eea` |
| gi | 直布罗陀 | `eu-eea` |
| gr | 希腊 | `eu-eea` |
| hk | 中国香港 | `hk-epd` |
| hu | 匈牙利 | `eu-eea` |
| ie | 爱尔兰 | `eu-eea` |
| jp | 日本 | `jp-moe` |
| kr | 韩国 | `kr-moe` |
| lv | 拉脱维亚 | `eu-eea` |
| lt | 立陶宛 | `eu-eea` |
| mo | 中国澳门 | `mo-smg` |
| mt | 马耳他 | `eu-eea` |
| nl | 荷兰 | `eu-eea` |
| mk | 北马其顿 | `eu-eea` |
| no | 挪威 | `eu-eea` |
| pl | 波兰 | `eu-eea` |
| pt | 葡萄牙 | `eu-eea` |
| ro | 罗马尼亚 | `eu-eea` |
| rs | 塞尔维亚 | `eu-eea` |
| sg | 新加坡 | `sg-nea``sg-nea-pm1h` |
| sk | 斯洛伐克 | `eu-eea` |
| si | 斯洛文尼亚 | `eu-eea` |
| es | 西班牙 | `eu-eea` |
| se | 瑞典 | `eu-eea` |
| ch | 瑞士 | `eu-eea` |
| tw | 中国台湾省 | `tw-me``tw-me-1h` |
| th | 泰国 | `th-pcd` |
| gb | 英国 | `gb-defra``eu-eea` |
| us | 美国 | `us-epa``us-epa-nc` |

<!-- END_DOC_40 -->

---


<!-- START_DOC_41: api_air-quality_air-daily-forecast.md (URL: https://dev.qweather.com/docs/api/air-quality/air-daily-forecast) -->
Title: 空气质量每日预报

URL Source: https://dev.qweather.com/docs/api/air-quality/air-daily-forecast

Markdown Content:
[和风天气开发者服务](https://dev.qweather.com/)

[控制台](https://id.qweather.com/#/login?redirect=https://console.qweather.com)

*   [首页](https://dev.qweather.com/)
*   [文档](https://dev.qweather.com/docs/)
*   [图标](https://icons.qweather.com/)
*   [展示](https://dev.qweather.com/showcase/)
*   [帮助](https://dev.qweather.com/help/)
*   [价格](https://dev.qweather.com/price/)
*   Search K 

*   [文档](https://dev.qweather.com/docs/)
*   >
*   [开发文档](https://dev.qweather.com/docs/api/)
*   >
*   [空气质量](https://dev.qweather.com/docs/api/air-quality/)
*   >
*   [空气质量每日预报](https://dev.qweather.com/docs/api/air-quality/air-daily-forecast/)

#### [开始使用](https://dev.qweather.com/docs/start/)

*   [特性](https://dev.qweather.com/docs/features/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [帐号管理](https://dev.qweather.com/docs/account/)

#### [开发文档](https://dev.qweather.com/docs/api/)

*   [GeoAPI](https://dev.qweather.com/docs/api/geoapi/)
*   [天气预报](https://dev.qweather.com/docs/api/weather/)
*   [分钟预报](https://dev.qweather.com/docs/api/minutely/)
*   [预警](https://dev.qweather.com/docs/api/warning/)
*   [天气指数](https://dev.qweather.com/docs/api/indices/)
*   [空气质量](https://dev.qweather.com/docs/api/air-quality/)

    *   [GET 实时空气质量](https://dev.qweather.com/docs/api/air-quality/air-current/)
    *   [GET 空气质量小时预报](https://dev.qweather.com/docs/api/air-quality/air-hourly-forecast/)
    *   [GET 空气质量每日预报](https://dev.qweather.com/docs/api/air-quality/air-daily-forecast/)
    *   [支持的空气质量指数](https://dev.qweather.com/docs/api/air-quality/aqi-list/)
    *   [污染物列表](https://dev.qweather.com/docs/api/air-quality/pollutant-list/)
    *   [空气质量覆盖范围](https://dev.qweather.com/docs/api/air-quality/aqi-coverage/)
    *   [健康影响和建议](https://dev.qweather.com/docs/api/air-quality/health-effect-advice/)
    *   [中国空气质量说明](https://dev.qweather.com/docs/api/air-quality/china-aqi/)

*   [时光机](https://dev.qweather.com/docs/api/time-machine/)
*   [热带气旋（台风）](https://dev.qweather.com/docs/api/tropical-cyclone/)
*   [海洋数据](https://dev.qweather.com/docs/api/ocean/)
*   [太阳辐射](https://dev.qweather.com/docs/api/solar-radiation/)
*   [天文](https://dev.qweather.com/docs/api/astronomy/)
*   [控制台API](https://dev.qweather.com/docs/api/console/)

#### [实用资料](https://dev.qweather.com/docs/resource/)

*   [专用词汇表](https://dev.qweather.com/docs/resource/glossary/)
*   [错误码](https://dev.qweather.com/docs/resource/error-code/)
*   [多语言](https://dev.qweather.com/docs/resource/language/)
*   [常见城市列表](https://dev.qweather.com/docs/resource/location-list/)
*   [元数据](https://dev.qweather.com/docs/resource/metadata/)
*   [单位](https://dev.qweather.com/docs/resource/unit/)

#### [最佳实践](https://dev.qweather.com/docs/best-practices/)

*   [不要假设](https://dev.qweather.com/docs/best-practices/no-assumptions/)
*   [优化请求](https://dev.qweather.com/docs/best-practices/optimize-requests/)
*   [处理Gzip](https://dev.qweather.com/docs/best-practices/gzip/)
*   [缓存你的数据](https://dev.qweather.com/docs/best-practices/cache/)
*   [安全指南](https://dev.qweather.com/docs/best-practices/security-guidelines/)

#### [财务与费用](https://dev.qweather.com/docs/finance/)

*   [计费方式和支付](https://dev.qweather.com/docs/finance/billing-and-payment/)
*   [按量计费定价](https://dev.qweather.com/docs/finance/pricing/)
*   [节省计划](https://dev.qweather.com/docs/finance/savings-plans/)
*   [增值税发票](https://dev.qweather.com/docs/finance/vat-invoice/)

#### [条款](https://dev.qweather.com/docs/terms/)

*   [使用限制](https://dev.qweather.com/docs/terms/restriction/)
*   [注明来源](https://dev.qweather.com/docs/terms/attribution/)
*   [服务条款](https://dev.qweather.com/docs/terms/tos/)

# 空气质量每日预报

空气质量每日预报API提供未来3天的空气质量（AQI）预报、污染物浓度值和健康建议。 
## 请求路径[#](https://dev.qweather.com/docs/api/air-quality/air-daily-forecast#endpoint)

`GET /airquality/v1/daily/{latitude}/{longitude}`

## 参数[#](https://dev.qweather.com/docs/api/air-quality/air-daily-forecast#parameters)

#### 路径参数

*   latitude 必选 number  所需位置的纬度。十进制，最多支持小数点后两位。例如 `39.92`  
*   longitude 必选 number  所需位置的经度。十进制，最多支持小数点后两位。例如 `116.41`  

#### 查询参数

*   lang string  [多语言设置](https://dev.qweather.com/docs/resource/language/)  

## 请求示例[#](https://dev.qweather.com/docs/api/air-quality/air-daily-forecast#request-example)

```bash
curl -X GET --compressed \
-H 'Authorization: Bearer your_token' \
'https://your-api-host/airquality/v1/daily/39.92/116.41'
```

请将`your_token`替换为你的[JWT身份认证](https://dev.qweather.com/docs/configuration/authentication/)，将`your_api_host`替换为你的[API Host](https://dev.qweather.com/docs/configuration/api-host/)

[试一下](https://dev.qweather.com/api-explore/#/Air%20Quality/getAirqualityDailyForecast)

## 返回数据[#](https://dev.qweather.com/docs/api/air-quality/air-daily-forecast#response)

```json
{
  "metadata": {
    "tag": "4b78230843e636a6f910631d94878da73aa980a66abfcf53d35f9c06493a292d",
    "attributions": [
      "https://developer.qweather.com/attribution.html"
    ]
  },
  "days": [
    {
      "forecastStartTime": "2023-02-14T23:00Z",
      "forecastEndTime": "2023-02-15T23:00Z",
      "indexes": [
        {
          "code": "qaqi",
          "name": "QAQI",
          "aqi": 1.0,
          "aqiDisplay": "1.0",
          "level": "1",
          "category": "Excellent",
          "color": {
            "red": 195,
            "green": 217,
            "blue": 78,
            "alpha": 1
          },
          "primaryPollutant": {
            "code": "pm2p5",
            "name": "PM 2.5",
            "fullName": "Fine particulate matter (<2.5µm)"
          },
          "health": {
            "effect": "No health implications.",
            "advice": {
              "generalPopulation": "Enjoy your outdoor activities.",
              "sensitivePopulation": "Enjoy your outdoor activities."
            }
          }
        },
        {
          "code": "eu-eea",
          "name": "EAQI (EU)",
          "aqi": 2,
          "aqiDisplay": "2",
          "level": "2",
          "category": "Fair",
          "color": {
            "red": 80,
            "green": 204,
            "blue": 170,
            "alpha": 1
          },
          "primaryPollutant": {
            "code": "pm2p5",
            "name": "PM 2.5",
            "fullName": "Fine particulate matter (<2.5µm)"
          },
          "health": {
            "effect": null,
            "advice": {
              "generalPopulation": "Enjoy your usual outdoor activities.",
              "sensitivePopulation": "Enjoy your usual outdoor activities."
            }
          }
        }
      ],
      "pollutants": [
        {
          "code": "pm2p5",
          "name": "PM 2.5",
          "fullName": "Fine particulate matter (<2.5µm)",
          "concentration": {
            "value": 11.88,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "eu-eea",
              "aqi": 2,
              "aqiDisplay": "2"
            },
            {
              "code": "qaqi",
              "aqi": 1,
              "aqiDisplay": "1"
            }
          ]
        },
        {
          "code": "pm10",
          "name": "PM 10",
          "fullName": "Inhalable particulate matter (<10µm)",
          "concentration": {
            "value": 2.38,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "eu-eea",
              "aqi": 1,
              "aqiDisplay": "1"
            },
            {
              "code": "qaqi",
              "aqi": 0.1,
              "aqiDisplay": "0.1"
            }
          ]
        },
        {
          "code": "no2",
          "name": "NO2",
          "fullName": "Nitrogen dioxide",
          "concentration": {
            "value": 4.1,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "eu-eea",
              "aqi": 1,
              "aqiDisplay": "1"
            },
            {
              "code": "qaqi",
              "aqi": 0.1,
              "aqiDisplay": "0.1"
            }
          ]
        },
        {
          "code": "o3",
          "name": "O3",
          "fullName": "Ozone",
          "concentration": {
            "value": 36.33,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "eu-eea",
              "aqi": 1,
              "aqiDisplay": "1"
            },
            {
              "code": "qaqi",
              "aqi": 0.8,
              "aqiDisplay": "0.8"
            }
          ]
        },
        {
          "code": "so2",
          "name": "SO2",
          "fullName": "Sulfur dioxide",
          "concentration": {
            "value": 32.13,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "eu-eea",
              "aqi": 1,
              "aqiDisplay": "1"
            },
            {
              "code": "qaqi",
              "aqi": 0.7,
              "aqiDisplay": "0.7"
            }
          ]
        }
      ]
    },
    {
      "forecastStartTime": "2023-02-15T23:00Z",
      "forecastEndTime": "2023-02-16T23:00Z",
      "indexes": [
        {
          "code": "qaqi",
          "name": "QAQI",
          "aqi": 1.1,
          "aqiDisplay": "1.1",
          "level": "1",
          "category": "Excellent",
          "color": {
            "red": 195,
            "green": 217,
            "blue": 78,
            "alpha": 1
          },
          "primaryPollutant": {
            "code": "o3",
            "name": "O3",
            "fullName": "Ozone"
          },
          "health": {
            "effect": "No health implications.",
            "advice": {
              "generalPopulation": "Enjoy your outdoor activities.",
              "sensitivePopulation": "Enjoy your outdoor activities."
            }
          }
        },
        {
          "code": "eu-eea",
          "name": "EAQI (EU)",
          "aqi": 2,
          "aqiDisplay": "2",
          "level": "2",
          "category": "Fair",
          "color": {
            "red": 80,
            "green": 204,
            "blue": 170,
            "alpha": 1
          },
          "primaryPollutant": {
            "code": "o3",
            "name": "O3",
            "fullName": "Ozone"
          },
          "health": {
            "effect": null,
            "advice": {
              "generalPopulation": "Enjoy your usual outdoor activities.",
              "sensitivePopulation": "Enjoy your usual outdoor activities."
            }
          }
        }
      ],
      "pollutants": [
        {
          "code": "pm2p5",
          "name": "PM 2.5",
          "fullName": "Fine particulate matter (<2.5µm)",
          "concentration": {
            "value": 9.13,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "eu-eea",
              "aqi": 1,
              "aqiDisplay": "1"
            },
            {
              "code": "qaqi",
              "aqi": 0.8,
              "aqiDisplay": "0.8"
            }
          ]
        },
        {
          "code": "pm10",
          "name": "PM 10",
          "fullName": "Inhalable particulate matter (<10µm)",
          "concentration": {
            "value": 1.08,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "eu-eea",
              "aqi": 1,
              "aqiDisplay": "1"
            },
            {
              "code": "qaqi",
              "aqi": 0.1,
              "aqiDisplay": "0.1"
            }
          ]
        },
        {
          "code": "no2",
          "name": "NO2",
          "fullName": "Nitrogen dioxide",
          "concentration": {
            "value": 2.34,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "eu-eea",
              "aqi": 1,
              "aqiDisplay": "1"
            },
            {
              "code": "qaqi",
              "aqi": 0.1,
              "aqiDisplay": "0.1"
            }
          ]
        },
        {
          "code": "o3",
          "name": "O3",
          "fullName": "Ozone",
          "concentration": {
            "value": 53.33,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "eu-eea",
              "aqi": 2,
              "aqiDisplay": "2"
            },
            {
              "code": "qaqi",
              "aqi": 1.1,
              "aqiDisplay": "1.1"
            }
          ]
        },
        {
          "code": "so2",
          "name": "SO2",
          "fullName": "Sulfur dioxide",
          "concentration": {
            "value": 22.17,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "eu-eea",
              "aqi": 1,
              "aqiDisplay": "1"
            },
            {
              "code": "qaqi",
              "aqi": 0.5,
              "aqiDisplay": "0.5"
            }
          ]
        }
      ]
    },
    {
      "forecastStartTime": "2023-02-16T23:00Z",
      "forecastEndTime": "2023-02-17T23:00Z",
      "indexes": [
        {
          "code": "qaqi",
          "name": "QAQI",
          "aqi": 0.9,
          "aqiDisplay": "0.9",
          "level": "1",
          "category": "Excellent",
          "color": {
            "red": 195,
            "green": 217,
            "blue": 78,
            "alpha": 1
          },
          "primaryPollutant": {
            "code": "o3",
            "name": "O3",
            "fullName": "Ozone"
          },
          "health": {
            "effect": "No health implications.",
            "advice": {
              "generalPopulation": "Enjoy your outdoor activities.",
              "sensitivePopulation": "Enjoy your outdoor activities."
            }
          }
        },
        {
          "code": "eu-eea",
          "name": "EAQI (EU)",
          "aqi": 1,
          "aqiDisplay": "1",
          "level": "1",
          "category": "Good",
          "color": {
            "red": 80,
            "green": 240,
            "blue": 230,
            "alpha": 1
          },
          "primaryPollutant": {
            "code": "o3",
            "name": "O3",
            "fullName": "Ozone"
          },
          "health": {
            "effect": null,
            "advice": {
              "generalPopulation": "The air quality is good. Enjoy your usual outdoor activities.",
              "sensitivePopulation": "The air quality is good. Enjoy your usual outdoor activities."
            }
          }
        }
      ],
      "pollutants": [
        {
          "code": "pm2p5",
          "name": "PM 2.5",
          "fullName": "Fine particulate matter (<2.5µm)",
          "concentration": {
            "value": 8.5,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "eu-eea",
              "aqi": 1,
              "aqiDisplay": "1"
            },
            {
              "code": "qaqi",
              "aqi": 0.7,
              "aqiDisplay": "0.7"
            }
          ]
        },
        {
          "code": "pm10",
          "name": "PM 10",
          "fullName": "Inhalable particulate matter (<10µm)",
          "concentration": {
            "value": 1.54,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "eu-eea",
              "aqi": 1,
              "aqiDisplay": "1"
            },
            {
              "code": "qaqi",
              "aqi": 0.1,
              "aqiDisplay": "0.1"
            }
          ]
        },
        {
          "code": "no2",
          "name": "NO2",
          "fullName": "Nitrogen dioxide",
          "concentration": {
            "value": 2.77,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "eu-eea",
              "aqi": 1,
              "aqiDisplay": "1"
            },
            {
              "code": "qaqi",
              "aqi": 0.1,
              "aqiDisplay": "0.1"
            }
          ]
        },
        {
          "code": "o3",
          "name": "O3",
          "fullName": "Ozone",
          "concentration": {
            "value": 43.86,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "eu-eea",
              "aqi": 1,
              "aqiDisplay": "1"
            },
            {
              "code": "qaqi",
              "aqi": 0.9,
              "aqiDisplay": "0.9"
            }
          ]
        },
        {
          "code": "so2",
          "name": "SO2",
          "fullName": "Sulfur dioxide",
          "concentration": {
            "value": 20.94,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "eu-eea",
              "aqi": 1,
              "aqiDisplay": "1"
            },
            {
              "code": "qaqi",
              "aqi": 0.5,
              "aqiDisplay": "0.5"
            }
          ]
        }
      ]
    }
  ]
}
```

*   metadata object  [元数据](https://dev.qweather.com/docs/resource/metadata/)  
    *   tag string  数据唯一标识  
    *   attributions array  数据归因信息或声明，必须与当前数据共同显示  

*   days array  每日空气质量数据列表  
    *   forecastStartTime date-time  预报数据的开始时间  
    *   forecastEndTime date-time  预报数据的结束时间(不含)  
    *   indexes array  空气质量指数列表，参考[支持的空气质量指数](https://dev.qweather.com/docs/api/air-quality/aqi-list/)  
        *   code string  空气质量指数的code  
        *   name string  空气质量指数的名字  
        *   aqi number  空气质量指数的数值，包括将非数字形式的指数值转换为数字后的结果，便于程序计算  
        *   aqiDisplay string  空气质量指数的标准显示值，可能包含非数字字符，向用户展示时建议使用该字段  
        *   level string  空气质量指数等级  
        *   category string  空气质量指数类别  
        *   color object  空气质量指数的 RGBA 颜色表示  
            *   red number  红色分量值，取值范围 [0, 255]  
            *   green number  绿色分量值，取值范围 [0, 255]  
            *   blue number  蓝色分量值，取值范围 [0, 255]  
            *   alpha number  透明度分量值，取值范围 [0, 1]  

        *   primaryPollutant object  首要[污染物](https://dev.qweather.com/docs/api/air-quality/pollutant-list/#primary-pollutant)表示导致当前空气污染的主要成分  
            *   code string  首要污染物的code  
            *   name string  首要污染物的名字  
            *   fullName string  首要污染物的全称  

        *   health object  空气质量对一般人群和敏感人群的[健康影响及指导建议](https://dev.qweather.com/docs/api/air-quality/health-effect-advice/)  
            *   effect string  空气质量对健康的影响，可能为空  
            *   advice object  健康建议，可能为空  
                *   generalPopulation string  对一般人群的健康指导意见，可能为空  
                *   sensitivePopulation string  对敏感人群的健康指导意见，可能为空  

    *   pollutants array  [污染物](https://dev.qweather.com/docs/api/air-quality/pollutant-list/)列表  
        *   code string  污染物code，可选值：`pm10`、`pm2p5`、`co`、`no`、`no2`、`so2`、`o3`、`nmhc`  
        *   name string  污染物的名字  
        *   fullName string  污染物的全称  
        *   concentration object  污染物的浓度值  
            *   value number  数值  
            *   unit string  [单位](https://dev.qweather.com/docs/resource/unit/)  

        *   subIndexes array  各项污染物的空气质量分指数列表，通常最差的污染物分指数代表当前的空气质量指数，并用于确定首要污染物  
            *   code string  分指数对应的空气质量指数代码  
            *   aqi number  分指数的数值  
            *   aqiDisplay string  分指数数值的显示名称  

#### 本页导航

*   [请求路径](https://dev.qweather.com/docs/api/air-quality/air-daily-forecast#endpoint)
*   [参数](https://dev.qweather.com/docs/api/air-quality/air-daily-forecast#parameters)
*   [请求示例](https://dev.qweather.com/docs/api/air-quality/air-daily-forecast#request-example)
*   [返回数据](https://dev.qweather.com/docs/api/air-quality/air-daily-forecast#response)

### [和风天气开发者服务](https://dev.qweather.com/)

[联系](https://www.qweather.com/contact/)• [博客](https://blog.qweather.com/)• [GitHub](https://github.com/qwd)

Language 

### 文档

*   [开始使用](https://dev.qweather.com/docs/start/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [开发文档](https://dev.qweather.com/docs/api/)
*   [财务与费用](https://dev.qweather.com/docs/finance/)
*   [最佳实践](https://dev.qweather.com/docs/best-practices/)
*   [实用资料](https://dev.qweather.com/docs/resource/)

### 特性

*   [服务和数据](https://dev.qweather.com/docs/features/service-and-data/)
*   [全球部署](https://dev.qweather.com/docs/features/global-deployment/)
*   [性能](https://dev.qweather.com/docs/features/performance/)

### 更多

*   [天气预报](https://www.qweather.com/)
*   [天气地图](https://map.qweather.com/)
*   [下载APP](https://www.qweather.com/app/)

*   © 2026 [QWeather](https://www.qweather.com/en/)
*   [许可协议](https://www.qweather.com/terms/developers-eula/)
*   [隐私政策](https://www.qweather.com/terms/privacy)
*   [免责声明](https://www.qweather.com/terms/disclaimer/)

[京ICP备15048401号-11](https://beian.miit.gov.cn/)

<!-- END_DOC_41 -->

---


<!-- START_DOC_42: api_air-quality_air-hourly-forecast.md (URL: https://dev.qweather.com/docs/api/air-quality/air-hourly-forecast) -->
Title: 空气质量小时预报

URL Source: https://dev.qweather.com/docs/api/air-quality/air-hourly-forecast

Markdown Content:
[和风天气开发者服务](https://dev.qweather.com/)

[控制台](https://id.qweather.com/#/login?redirect=https://console.qweather.com)

*   [首页](https://dev.qweather.com/)
*   [文档](https://dev.qweather.com/docs/)
*   [图标](https://icons.qweather.com/)
*   [展示](https://dev.qweather.com/showcase/)
*   [帮助](https://dev.qweather.com/help/)
*   [价格](https://dev.qweather.com/price/)
*   Search K 

*   [文档](https://dev.qweather.com/docs/)
*   >
*   [开发文档](https://dev.qweather.com/docs/api/)
*   >
*   [空气质量](https://dev.qweather.com/docs/api/air-quality/)
*   >
*   [空气质量小时预报](https://dev.qweather.com/docs/api/air-quality/air-hourly-forecast/)

#### [开始使用](https://dev.qweather.com/docs/start/)

*   [特性](https://dev.qweather.com/docs/features/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [帐号管理](https://dev.qweather.com/docs/account/)

#### [开发文档](https://dev.qweather.com/docs/api/)

*   [GeoAPI](https://dev.qweather.com/docs/api/geoapi/)
*   [天气预报](https://dev.qweather.com/docs/api/weather/)
*   [分钟预报](https://dev.qweather.com/docs/api/minutely/)
*   [预警](https://dev.qweather.com/docs/api/warning/)
*   [天气指数](https://dev.qweather.com/docs/api/indices/)
*   [空气质量](https://dev.qweather.com/docs/api/air-quality/)

    *   [GET 实时空气质量](https://dev.qweather.com/docs/api/air-quality/air-current/)
    *   [GET 空气质量小时预报](https://dev.qweather.com/docs/api/air-quality/air-hourly-forecast/)
    *   [GET 空气质量每日预报](https://dev.qweather.com/docs/api/air-quality/air-daily-forecast/)
    *   [支持的空气质量指数](https://dev.qweather.com/docs/api/air-quality/aqi-list/)
    *   [污染物列表](https://dev.qweather.com/docs/api/air-quality/pollutant-list/)
    *   [空气质量覆盖范围](https://dev.qweather.com/docs/api/air-quality/aqi-coverage/)
    *   [健康影响和建议](https://dev.qweather.com/docs/api/air-quality/health-effect-advice/)
    *   [中国空气质量说明](https://dev.qweather.com/docs/api/air-quality/china-aqi/)

*   [时光机](https://dev.qweather.com/docs/api/time-machine/)
*   [热带气旋（台风）](https://dev.qweather.com/docs/api/tropical-cyclone/)
*   [海洋数据](https://dev.qweather.com/docs/api/ocean/)
*   [太阳辐射](https://dev.qweather.com/docs/api/solar-radiation/)
*   [天文](https://dev.qweather.com/docs/api/astronomy/)
*   [控制台API](https://dev.qweather.com/docs/api/console/)

#### [实用资料](https://dev.qweather.com/docs/resource/)

*   [专用词汇表](https://dev.qweather.com/docs/resource/glossary/)
*   [错误码](https://dev.qweather.com/docs/resource/error-code/)
*   [多语言](https://dev.qweather.com/docs/resource/language/)
*   [常见城市列表](https://dev.qweather.com/docs/resource/location-list/)
*   [元数据](https://dev.qweather.com/docs/resource/metadata/)
*   [单位](https://dev.qweather.com/docs/resource/unit/)

#### [最佳实践](https://dev.qweather.com/docs/best-practices/)

*   [不要假设](https://dev.qweather.com/docs/best-practices/no-assumptions/)
*   [优化请求](https://dev.qweather.com/docs/best-practices/optimize-requests/)
*   [处理Gzip](https://dev.qweather.com/docs/best-practices/gzip/)
*   [缓存你的数据](https://dev.qweather.com/docs/best-practices/cache/)
*   [安全指南](https://dev.qweather.com/docs/best-practices/security-guidelines/)

#### [财务与费用](https://dev.qweather.com/docs/finance/)

*   [计费方式和支付](https://dev.qweather.com/docs/finance/billing-and-payment/)
*   [按量计费定价](https://dev.qweather.com/docs/finance/pricing/)
*   [节省计划](https://dev.qweather.com/docs/finance/savings-plans/)
*   [增值税发票](https://dev.qweather.com/docs/finance/vat-invoice/)

#### [条款](https://dev.qweather.com/docs/terms/)

*   [使用限制](https://dev.qweather.com/docs/terms/restriction/)
*   [注明来源](https://dev.qweather.com/docs/terms/attribution/)
*   [服务条款](https://dev.qweather.com/docs/terms/tos/)

# 空气质量小时预报

空气质量小时预报API提供未来24小时空气质量的数据，包括AQI、污染物浓度、分指数以及健康建议。 
## 请求路径[#](https://dev.qweather.com/docs/api/air-quality/air-hourly-forecast#endpoint)

`GET /airquality/v1/hourly/{latitude}/{longitude}`

## 参数[#](https://dev.qweather.com/docs/api/air-quality/air-hourly-forecast#parameters)

#### 路径参数

*   latitude 必选 number  所需位置的纬度。十进制，最多支持小数点后两位。例如 `39.92`  
*   longitude 必选 number  所需位置的经度。十进制，最多支持小数点后两位。例如 `116.41`  

#### 查询参数

*   lang string  [多语言设置](https://dev.qweather.com/docs/resource/language/)  

## 请求示例[#](https://dev.qweather.com/docs/api/air-quality/air-hourly-forecast#request-example)

```bash
curl -X GET --compressed \
-H 'Authorization: Bearer your_token' \
'https://your-api-host/airquality/v1/hourly/39.92/116.41'
```

请将`your_token`替换为你的[JWT身份认证](https://dev.qweather.com/docs/configuration/authentication/)，将`your_api_host`替换为你的[API Host](https://dev.qweather.com/docs/configuration/api-host/)

[试一下](https://dev.qweather.com/api-explore/#/Air%20Quality/getAirqualityHourlyForecast)

## 返回数据[#](https://dev.qweather.com/docs/api/air-quality/air-hourly-forecast#response)

```json
{
  "metadata": {
    "tag": "b1d735802464094bf274fd2165309ddfdab22cec2fa0e644edfcd7f803c2aaad",
    "attributions": [
      "https://developer.qweather.com/attribution.html"
    ]
  },
  "hours": [
    {
      "forecastTime": "2023-05-17T03:00Z",
      "indexes": [
        {
          "code": "qaqi",
          "name": "QAQI",
          "aqi": 1.4,
          "aqiDisplay": "1.4",
          "level": "1",
          "category": "Excellent",
          "color": {
            "red": 195,
            "green": 217,
            "blue": 78,
            "alpha": 1
          },
          "primaryPollutant": {
            "code": "pm2p5",
            "name": "PM 2.5",
            "fullName": "Fine particulate matter (<2.5µm)"
          },
          "health": {
            "effect": "No health implications.",
            "advice": {
              "generalPopulation": "Enjoy your outdoor activities.",
              "sensitivePopulation": "Enjoy your outdoor activities."
            }
          }
        },
        {
          "code": "gb-defra",
          "name": "DAQI (GB)",
          "aqi": 2,
          "aqiDisplay": "2",
          "level": "1",
          "category": "Low",
          "color": {
            "red": 49,
            "green": 255,
            "blue": 0,
            "alpha": 1
          },
          "primaryPollutant": {
            "code": "pm2p5",
            "name": "PM 2.5",
            "fullName": "Fine particulate matter (<2.5µm)"
          },
          "health": {
            "effect": null,
            "advice": {
              "generalPopulation": "Enjoy your usual outdoor activities.",
              "sensitivePopulation": "Enjoy your usual outdoor activities."
            }
          }
        }
      ],
      "pollutants": [
        {
          "code": "pm2p5",
          "name": "PM 2.5",
          "fullName": "Fine particulate matter (<2.5µm)",
          "concentration": {
            "value": 17.01,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 1.4,
              "aqiDisplay": "1.4"
            }
          ]
        },
        {
          "code": "pm10",
          "name": "PM 10",
          "fullName": "Inhalable particulate matter (<10µm)",
          "concentration": {
            "value": 2.88,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.2,
              "aqiDisplay": "0.2"
            }
          ]
        },
        {
          "code": "no2",
          "name": "NO2",
          "fullName": "Nitrogen dioxide",
          "concentration": {
            "value": 4.05,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.1,
              "aqiDisplay": "0.1"
            }
          ]
        },
        {
          "code": "o3",
          "name": "O3",
          "fullName": "Ozone",
          "concentration": {
            "value": 6.55,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.2,
              "aqiDisplay": "0.2"
            }
          ]
        },
        {
          "code": "so2",
          "name": "SO2",
          "fullName": "Sulfur dioxide",
          "concentration": {
            "value": 49.05,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 1,
              "aqiDisplay": "1"
            }
          ]
        }
      ]
    },
    {
      "forecastTime": "2023-05-17T04:00Z",
      "indexes": [
        {
          "code": "qaqi",
          "name": "QAQI",
          "aqi": 1.4,
          "aqiDisplay": "1.4",
          "level": "1",
          "category": "Excellent",
          "color": {
            "red": 195,
            "green": 217,
            "blue": 78,
            "alpha": 1
          },
          "primaryPollutant": {
            "code": "pm2p5",
            "name": "PM 2.5",
            "fullName": "Fine particulate matter (<2.5µm)"
          },
          "health": {
            "effect": "No health implications.",
            "advice": {
              "generalPopulation": "Enjoy your outdoor activities.",
              "sensitivePopulation": "Enjoy your outdoor activities."
            }
          }
        },
        {
          "code": "gb-defra",
          "name": "DAQI (GB)",
          "aqi": 2,
          "aqiDisplay": "2",
          "level": "1",
          "category": "Low",
          "color": {
            "red": 49,
            "green": 255,
            "blue": 0,
            "alpha": 1
          },
          "primaryPollutant": {
            "code": "pm2p5",
            "name": "PM 2.5",
            "fullName": "Fine particulate matter (<2.5µm)"
          },
          "health": {
            "effect": null,
            "advice": {
              "generalPopulation": "Enjoy your usual outdoor activities.",
              "sensitivePopulation": "Enjoy your usual outdoor activities."
            }
          }
        }
      ],
      "pollutants": [
        {
          "code": "pm2p5",
          "name": "PM 2.5",
          "fullName": "Fine particulate matter (<2.5µm)",
          "concentration": {
            "value": 16.89,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 1.4,
              "aqiDisplay": "1.4"
            }
          ]
        },
        {
          "code": "pm10",
          "name": "PM 10",
          "fullName": "Inhalable particulate matter (<10µm)",
          "concentration": {
            "value": 2.84,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.2,
              "aqiDisplay": "0.2"
            }
          ]
        },
        {
          "code": "no2",
          "name": "NO2",
          "fullName": "Nitrogen dioxide",
          "concentration": {
            "value": 3.91,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.1,
              "aqiDisplay": "0.1"
            }
          ]
        },
        {
          "code": "o3",
          "name": "O3",
          "fullName": "Ozone",
          "concentration": {
            "value": 6.21,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.2,
              "aqiDisplay": "0.2"
            }
          ]
        },
        {
          "code": "so2",
          "name": "SO2",
          "fullName": "Sulfur dioxide",
          "concentration": {
            "value": 47.75,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 1,
              "aqiDisplay": "1"
            }
          ]
        }
      ]
    },
    {
      "forecastTime": "2023-05-17T05:00Z",
      "indexes": [
        {
          "code": "qaqi",
          "name": "QAQI",
          "aqi": 1.4,
          "aqiDisplay": "1.4",
          "level": "1",
          "category": "Excellent",
          "color": {
            "red": 195,
            "green": 217,
            "blue": 78,
            "alpha": 1
          },
          "primaryPollutant": {
            "code": "pm2p5",
            "name": "PM 2.5",
            "fullName": "Fine particulate matter (<2.5µm)"
          },
          "health": {
            "effect": "No health implications.",
            "advice": {
              "generalPopulation": "Enjoy your outdoor activities.",
              "sensitivePopulation": "Enjoy your outdoor activities."
            }
          }
        },
        {
          "code": "gb-defra",
          "name": "DAQI (GB)",
          "aqi": 2,
          "aqiDisplay": "2",
          "level": "1",
          "category": "Low",
          "color": {
            "red": 49,
            "green": 255,
            "blue": 0,
            "alpha": 1
          },
          "primaryPollutant": {
            "code": "pm2p5",
            "name": "PM 2.5",
            "fullName": "Fine particulate matter (<2.5µm)"
          },
          "health": {
            "effect": null,
            "advice": {
              "generalPopulation": "Enjoy your usual outdoor activities.",
              "sensitivePopulation": "Enjoy your usual outdoor activities."
            }
          }
        }
      ],
      "pollutants": [
        {
          "code": "pm2p5",
          "name": "PM 2.5",
          "fullName": "Fine particulate matter (<2.5µm)",
          "concentration": {
            "value": 16.56,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 1.4,
              "aqiDisplay": "1.4"
            }
          ]
        },
        {
          "code": "pm10",
          "name": "PM 10",
          "fullName": "Inhalable particulate matter (<10µm)",
          "concentration": {
            "value": 2.88,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.2,
              "aqiDisplay": "0.2"
            }
          ]
        },
        {
          "code": "no2",
          "name": "NO2",
          "fullName": "Nitrogen dioxide",
          "concentration": {
            "value": 4.57,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.1,
              "aqiDisplay": "0.1"
            }
          ]
        },
        {
          "code": "o3",
          "name": "O3",
          "fullName": "Ozone",
          "concentration": {
            "value": 5.62,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.2,
              "aqiDisplay": "0.2"
            }
          ]
        },
        {
          "code": "so2",
          "name": "SO2",
          "fullName": "Sulfur dioxide",
          "concentration": {
            "value": 46.13,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 1,
              "aqiDisplay": "1"
            }
          ]
        }
      ]
    },
    {
      "forecastTime": "2023-05-17T06:00Z",
      "indexes": [
        {
          "code": "qaqi",
          "name": "QAQI",
          "aqi": 1.4,
          "aqiDisplay": "1.4",
          "level": "1",
          "category": "Excellent",
          "color": {
            "red": 195,
            "green": 217,
            "blue": 78,
            "alpha": 1
          },
          "primaryPollutant": {
            "code": "pm2p5",
            "name": "PM 2.5",
            "fullName": "Fine particulate matter (<2.5µm)"
          },
          "health": {
            "effect": "No health implications.",
            "advice": {
              "generalPopulation": "Enjoy your outdoor activities.",
              "sensitivePopulation": "Enjoy your outdoor activities."
            }
          }
        },
        {
          "code": "gb-defra",
          "name": "DAQI (GB)",
          "aqi": 2,
          "aqiDisplay": "2",
          "level": "1",
          "category": "Low",
          "color": {
            "red": 49,
            "green": 255,
            "blue": 0,
            "alpha": 1
          },
          "primaryPollutant": {
            "code": "pm2p5",
            "name": "PM 2.5",
            "fullName": "Fine particulate matter (<2.5µm)"
          },
          "health": {
            "effect": null,
            "advice": {
              "generalPopulation": "Enjoy your usual outdoor activities.",
              "sensitivePopulation": "Enjoy your usual outdoor activities."
            }
          }
        }
      ],
      "pollutants": [
        {
          "code": "pm2p5",
          "name": "PM 2.5",
          "fullName": "Fine particulate matter (<2.5µm)",
          "concentration": {
            "value": 16.85,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 1.4,
              "aqiDisplay": "1.4"
            }
          ]
        },
        {
          "code": "pm10",
          "name": "PM 10",
          "fullName": "Inhalable particulate matter (<10µm)",
          "concentration": {
            "value": 3.03,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.2,
              "aqiDisplay": "0.2"
            }
          ]
        },
        {
          "code": "no2",
          "name": "NO2",
          "fullName": "Nitrogen dioxide",
          "concentration": {
            "value": 5.79,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.2,
              "aqiDisplay": "0.2"
            }
          ]
        },
        {
          "code": "o3",
          "name": "O3",
          "fullName": "Ozone",
          "concentration": {
            "value": 3.68,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.1,
              "aqiDisplay": "0.1"
            }
          ]
        },
        {
          "code": "so2",
          "name": "SO2",
          "fullName": "Sulfur dioxide",
          "concentration": {
            "value": 45.32,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 1,
              "aqiDisplay": "1"
            }
          ]
        }
      ]
    },
    {
      "forecastTime": "2023-05-17T07:00Z",
      "indexes": [
        {
          "code": "qaqi",
          "name": "QAQI",
          "aqi": 1.5,
          "aqiDisplay": "1.5",
          "level": "1",
          "category": "Excellent",
          "color": {
            "red": 195,
            "green": 217,
            "blue": 78,
            "alpha": 1
          },
          "primaryPollutant": {
            "code": "pm2p5",
            "name": "PM 2.5",
            "fullName": "Fine particulate matter (<2.5µm)"
          },
          "health": {
            "effect": "No health implications.",
            "advice": {
              "generalPopulation": "Enjoy your outdoor activities.",
              "sensitivePopulation": "Enjoy your outdoor activities."
            }
          }
        },
        {
          "code": "gb-defra",
          "name": "DAQI (GB)",
          "aqi": 2,
          "aqiDisplay": "2",
          "level": "1",
          "category": "Low",
          "color": {
            "red": 49,
            "green": 255,
            "blue": 0,
            "alpha": 1
          },
          "primaryPollutant": {
            "code": "pm2p5",
            "name": "PM 2.5",
            "fullName": "Fine particulate matter (<2.5µm)"
          },
          "health": {
            "effect": null,
            "advice": {
              "generalPopulation": "Enjoy your usual outdoor activities.",
              "sensitivePopulation": "Enjoy your usual outdoor activities."
            }
          }
        }
      ],
      "pollutants": [
        {
          "code": "pm2p5",
          "name": "PM 2.5",
          "fullName": "Fine particulate matter (<2.5µm)",
          "concentration": {
            "value": 17.75,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 1.5,
              "aqiDisplay": "1.5"
            }
          ]
        },
        {
          "code": "pm10",
          "name": "PM 10",
          "fullName": "Inhalable particulate matter (<10µm)",
          "concentration": {
            "value": 3.06,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.2,
              "aqiDisplay": "0.2"
            }
          ]
        },
        {
          "code": "no2",
          "name": "NO2",
          "fullName": "Nitrogen dioxide",
          "concentration": {
            "value": 7.12,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.2,
              "aqiDisplay": "0.2"
            }
          ]
        },
        {
          "code": "o3",
          "name": "O3",
          "fullName": "Ozone",
          "concentration": {
            "value": 2.66,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.1,
              "aqiDisplay": "0.1"
            }
          ]
        },
        {
          "code": "so2",
          "name": "SO2",
          "fullName": "Sulfur dioxide",
          "concentration": {
            "value": 48.14,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 1,
              "aqiDisplay": "1"
            }
          ]
        }
      ]
    },
    {
      "forecastTime": "2023-05-17T08:00Z",
      "indexes": [
        {
          "code": "qaqi",
          "name": "QAQI",
          "aqi": 1.7,
          "aqiDisplay": "1.7",
          "level": "1",
          "category": "Excellent",
          "color": {
            "red": 195,
            "green": 217,
            "blue": 78,
            "alpha": 1
          },
          "primaryPollutant": {
            "code": "pm2p5",
            "name": "PM 2.5",
            "fullName": "Fine particulate matter (<2.5µm)"
          },
          "health": {
            "effect": "No health implications.",
            "advice": {
              "generalPopulation": "Enjoy your outdoor activities.",
              "sensitivePopulation": "Enjoy your outdoor activities."
            }
          }
        },
        {
          "code": "gb-defra",
          "name": "DAQI (GB)",
          "aqi": 2,
          "aqiDisplay": "2",
          "level": "1",
          "category": "Low",
          "color": {
            "red": 49,
            "green": 255,
            "blue": 0,
            "alpha": 1
          },
          "primaryPollutant": {
            "code": "pm2p5",
            "name": "PM 2.5",
            "fullName": "Fine particulate matter (<2.5µm)"
          },
          "health": {
            "effect": null,
            "advice": {
              "generalPopulation": "Enjoy your usual outdoor activities.",
              "sensitivePopulation": "Enjoy your usual outdoor activities."
            }
          }
        }
      ],
      "pollutants": [
        {
          "code": "pm2p5",
          "name": "PM 2.5",
          "fullName": "Fine particulate matter (<2.5µm)",
          "concentration": {
            "value": 20.19,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 1.7,
              "aqiDisplay": "1.7"
            }
          ]
        },
        {
          "code": "pm10",
          "name": "PM 10",
          "fullName": "Inhalable particulate matter (<10µm)",
          "concentration": {
            "value": 2.94,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.2,
              "aqiDisplay": "0.2"
            }
          ]
        },
        {
          "code": "no2",
          "name": "NO2",
          "fullName": "Nitrogen dioxide",
          "concentration": {
            "value": 8.14,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.2,
              "aqiDisplay": "0.2"
            }
          ]
        },
        {
          "code": "o3",
          "name": "O3",
          "fullName": "Ozone",
          "concentration": {
            "value": 7.61,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.2,
              "aqiDisplay": "0.2"
            }
          ]
        },
        {
          "code": "so2",
          "name": "SO2",
          "fullName": "Sulfur dioxide",
          "concentration": {
            "value": 39.06,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.8,
              "aqiDisplay": "0.8"
            }
          ]
        }
      ]
    },
    {
      "forecastTime": "2023-05-17T09:00Z",
      "indexes": [
        {
          "code": "qaqi",
          "name": "QAQI",
          "aqi": 1.6,
          "aqiDisplay": "1.6",
          "level": "1",
          "category": "Excellent",
          "color": {
            "red": 195,
            "green": 217,
            "blue": 78,
            "alpha": 1
          },
          "primaryPollutant": {
            "code": "pm2p5",
            "name": "PM 2.5",
            "fullName": "Fine particulate matter (<2.5µm)"
          },
          "health": {
            "effect": "No health implications.",
            "advice": {
              "generalPopulation": "Enjoy your outdoor activities.",
              "sensitivePopulation": "Enjoy your outdoor activities."
            }
          }
        },
        {
          "code": "gb-defra",
          "name": "DAQI (GB)",
          "aqi": 2,
          "aqiDisplay": "2",
          "level": "1",
          "category": "Low",
          "color": {
            "red": 49,
            "green": 255,
            "blue": 0,
            "alpha": 1
          },
          "primaryPollutant": {
            "code": "pm2p5",
            "name": "PM 2.5",
            "fullName": "Fine particulate matter (<2.5µm)"
          },
          "health": {
            "effect": null,
            "advice": {
              "generalPopulation": "Enjoy your usual outdoor activities.",
              "sensitivePopulation": "Enjoy your usual outdoor activities."
            }
          }
        }
      ],
      "pollutants": [
        {
          "code": "pm2p5",
          "name": "PM 2.5",
          "fullName": "Fine particulate matter (<2.5µm)",
          "concentration": {
            "value": 19.98,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 1.6,
              "aqiDisplay": "1.6"
            }
          ]
        },
        {
          "code": "pm10",
          "name": "PM 10",
          "fullName": "Inhalable particulate matter (<10µm)",
          "concentration": {
            "value": 2.85,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.2,
              "aqiDisplay": "0.2"
            }
          ]
        },
        {
          "code": "no2",
          "name": "NO2",
          "fullName": "Nitrogen dioxide",
          "concentration": {
            "value": 6.73,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.2,
              "aqiDisplay": "0.2"
            }
          ]
        },
        {
          "code": "o3",
          "name": "O3",
          "fullName": "Ozone",
          "concentration": {
            "value": 25.89,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.6,
              "aqiDisplay": "0.6"
            }
          ]
        },
        {
          "code": "so2",
          "name": "SO2",
          "fullName": "Sulfur dioxide",
          "concentration": {
            "value": 32.27,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.7,
              "aqiDisplay": "0.7"
            }
          ]
        }
      ]
    },
    {
      "forecastTime": "2023-05-17T10:00Z",
      "indexes": [
        {
          "code": "qaqi",
          "name": "QAQI",
          "aqi": 1.4,
          "aqiDisplay": "1.4",
          "level": "1",
          "category": "Excellent",
          "color": {
            "red": 195,
            "green": 217,
            "blue": 78,
            "alpha": 1
          },
          "primaryPollutant": {
            "code": "pm2p5",
            "name": "PM 2.5",
            "fullName": "Fine particulate matter (<2.5µm)"
          },
          "health": {
            "effect": "No health implications.",
            "advice": {
              "generalPopulation": "Enjoy your outdoor activities.",
              "sensitivePopulation": "Enjoy your outdoor activities."
            }
          }
        },
        {
          "code": "gb-defra",
          "name": "DAQI (GB)",
          "aqi": 2,
          "aqiDisplay": "2",
          "level": "1",
          "category": "Low",
          "color": {
            "red": 49,
            "green": 255,
            "blue": 0,
            "alpha": 1
          },
          "primaryPollutant": {
            "code": "o3",
            "name": "O3",
            "fullName": "Ozone"
          },
          "health": {
            "effect": null,
            "advice": {
              "generalPopulation": "Enjoy your usual outdoor activities.",
              "sensitivePopulation": "Enjoy your usual outdoor activities."
            }
          }
        }
      ],
      "pollutants": [
        {
          "code": "pm2p5",
          "name": "PM 2.5",
          "fullName": "Fine particulate matter (<2.5µm)",
          "concentration": {
            "value": 16.63,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 1.4,
              "aqiDisplay": "1.4"
            }
          ]
        },
        {
          "code": "pm10",
          "name": "PM 10",
          "fullName": "Inhalable particulate matter (<10µm)",
          "concentration": {
            "value": 2.92,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.2,
              "aqiDisplay": "0.2"
            }
          ]
        },
        {
          "code": "no2",
          "name": "NO2",
          "fullName": "Nitrogen dioxide",
          "concentration": {
            "value": 6.03,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.2,
              "aqiDisplay": "0.2"
            }
          ]
        },
        {
          "code": "o3",
          "name": "O3",
          "fullName": "Ozone",
          "concentration": {
            "value": 40.21,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.9,
              "aqiDisplay": "0.9"
            }
          ]
        },
        {
          "code": "so2",
          "name": "SO2",
          "fullName": "Sulfur dioxide",
          "concentration": {
            "value": 27.15,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.6,
              "aqiDisplay": "0.6"
            }
          ]
        }
      ]
    },
    {
      "forecastTime": "2023-05-17T11:00Z",
      "indexes": [
        {
          "code": "qaqi",
          "name": "QAQI",
          "aqi": 1.1,
          "aqiDisplay": "1.1",
          "level": "1",
          "category": "Excellent",
          "color": {
            "red": 195,
            "green": 217,
            "blue": 78,
            "alpha": 1
          },
          "primaryPollutant": {
            "code": "o3",
            "name": "O3",
            "fullName": "Ozone"
          },
          "health": {
            "effect": "No health implications.",
            "advice": {
              "generalPopulation": "Enjoy your outdoor activities.",
              "sensitivePopulation": "Enjoy your outdoor activities."
            }
          }
        },
        {
          "code": "gb-defra",
          "name": "DAQI (GB)",
          "aqi": 2,
          "aqiDisplay": "2",
          "level": "1",
          "category": "Low",
          "color": {
            "red": 49,
            "green": 255,
            "blue": 0,
            "alpha": 1
          },
          "primaryPollutant": {
            "code": "o3",
            "name": "O3",
            "fullName": "Ozone"
          },
          "health": {
            "effect": null,
            "advice": {
              "generalPopulation": "Enjoy your usual outdoor activities.",
              "sensitivePopulation": "Enjoy your usual outdoor activities."
            }
          }
        }
      ],
      "pollutants": [
        {
          "code": "pm2p5",
          "name": "PM 2.5",
          "fullName": "Fine particulate matter (<2.5µm)",
          "concentration": {
            "value": 11.35,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 1,
              "aqiDisplay": "1"
            }
          ]
        },
        {
          "code": "pm10",
          "name": "PM 10",
          "fullName": "Inhalable particulate matter (<10µm)",
          "concentration": {
            "value": 3.19,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.2,
              "aqiDisplay": "0.2"
            }
          ]
        },
        {
          "code": "no2",
          "name": "NO2",
          "fullName": "Nitrogen dioxide",
          "concentration": {
            "value": 3.59,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.1,
              "aqiDisplay": "0.1"
            }
          ]
        },
        {
          "code": "o3",
          "name": "O3",
          "fullName": "Ozone",
          "concentration": {
            "value": 50.73,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 1.1,
              "aqiDisplay": "1.1"
            }
          ]
        },
        {
          "code": "so2",
          "name": "SO2",
          "fullName": "Sulfur dioxide",
          "concentration": {
            "value": 21.62,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.5,
              "aqiDisplay": "0.5"
            }
          ]
        }
      ]
    },
    {
      "forecastTime": "2023-05-17T12:00Z",
      "indexes": [
        {
          "code": "qaqi",
          "name": "QAQI",
          "aqi": 1.3,
          "aqiDisplay": "1.3",
          "level": "1",
          "category": "Excellent",
          "color": {
            "red": 195,
            "green": 217,
            "blue": 78,
            "alpha": 1
          },
          "primaryPollutant": {
            "code": "o3",
            "name": "O3",
            "fullName": "Ozone"
          },
          "health": {
            "effect": "No health implications.",
            "advice": {
              "generalPopulation": "Enjoy your outdoor activities.",
              "sensitivePopulation": "Enjoy your outdoor activities."
            }
          }
        },
        {
          "code": "gb-defra",
          "name": "DAQI (GB)",
          "aqi": 2,
          "aqiDisplay": "2",
          "level": "1",
          "category": "Low",
          "color": {
            "red": 49,
            "green": 255,
            "blue": 0,
            "alpha": 1
          },
          "primaryPollutant": {
            "code": "o3",
            "name": "O3",
            "fullName": "Ozone"
          },
          "health": {
            "effect": null,
            "advice": {
              "generalPopulation": "Enjoy your usual outdoor activities.",
              "sensitivePopulation": "Enjoy your usual outdoor activities."
            }
          }
        }
      ],
      "pollutants": [
        {
          "code": "pm2p5",
          "name": "PM 2.5",
          "fullName": "Fine particulate matter (<2.5µm)",
          "concentration": {
            "value": 7.76,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.7,
              "aqiDisplay": "0.7"
            }
          ]
        },
        {
          "code": "pm10",
          "name": "PM 10",
          "fullName": "Inhalable particulate matter (<10µm)",
          "concentration": {
            "value": 3.45,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.2,
              "aqiDisplay": "0.2"
            }
          ]
        },
        {
          "code": "no2",
          "name": "NO2",
          "fullName": "Nitrogen dioxide",
          "concentration": {
            "value": 2.79,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.1,
              "aqiDisplay": "0.1"
            }
          ]
        },
        {
          "code": "o3",
          "name": "O3",
          "fullName": "Ozone",
          "concentration": {
            "value": 60.19,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 1.3,
              "aqiDisplay": "1.3"
            }
          ]
        },
        {
          "code": "so2",
          "name": "SO2",
          "fullName": "Sulfur dioxide",
          "concentration": {
            "value": 13.38,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.3,
              "aqiDisplay": "0.3"
            }
          ]
        }
      ]
    },
    {
      "forecastTime": "2023-05-17T13:00Z",
      "indexes": [
        {
          "code": "qaqi",
          "name": "QAQI",
          "aqi": 1.4,
          "aqiDisplay": "1.4",
          "level": "1",
          "category": "Excellent",
          "color": {
            "red": 195,
            "green": 217,
            "blue": 78,
            "alpha": 1
          },
          "primaryPollutant": {
            "code": "o3",
            "name": "O3",
            "fullName": "Ozone"
          },
          "health": {
            "effect": "No health implications.",
            "advice": {
              "generalPopulation": "Enjoy your outdoor activities.",
              "sensitivePopulation": "Enjoy your outdoor activities."
            }
          }
        },
        {
          "code": "gb-defra",
          "name": "DAQI (GB)",
          "aqi": 2,
          "aqiDisplay": "2",
          "level": "1",
          "category": "Low",
          "color": {
            "red": 49,
            "green": 255,
            "blue": 0,
            "alpha": 1
          },
          "primaryPollutant": {
            "code": "o3",
            "name": "O3",
            "fullName": "Ozone"
          },
          "health": {
            "effect": null,
            "advice": {
              "generalPopulation": "Enjoy your usual outdoor activities.",
              "sensitivePopulation": "Enjoy your usual outdoor activities."
            }
          }
        }
      ],
      "pollutants": [
        {
          "code": "pm2p5",
          "name": "PM 2.5",
          "fullName": "Fine particulate matter (<2.5µm)",
          "concentration": {
            "value": 6.43,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.6,
              "aqiDisplay": "0.6"
            }
          ]
        },
        {
          "code": "pm10",
          "name": "PM 10",
          "fullName": "Inhalable particulate matter (<10µm)",
          "concentration": {
            "value": 3.37,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.2,
              "aqiDisplay": "0.2"
            }
          ]
        },
        {
          "code": "no2",
          "name": "NO2",
          "fullName": "Nitrogen dioxide",
          "concentration": {
            "value": 2.08,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.1,
              "aqiDisplay": "0.1"
            }
          ]
        },
        {
          "code": "o3",
          "name": "O3",
          "fullName": "Ozone",
          "concentration": {
            "value": 65.1,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 1.4,
              "aqiDisplay": "1.4"
            }
          ]
        },
        {
          "code": "so2",
          "name": "SO2",
          "fullName": "Sulfur dioxide",
          "concentration": {
            "value": 10.41,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.3,
              "aqiDisplay": "0.3"
            }
          ]
        }
      ]
    },
    {
      "forecastTime": "2023-05-17T14:00Z",
      "indexes": [
        {
          "code": "qaqi",
          "name": "QAQI",
          "aqi": 1.5,
          "aqiDisplay": "1.5",
          "level": "1",
          "category": "Excellent",
          "color": {
            "red": 195,
            "green": 217,
            "blue": 78,
            "alpha": 1
          },
          "primaryPollutant": {
            "code": "o3",
            "name": "O3",
            "fullName": "Ozone"
          },
          "health": {
            "effect": "No health implications.",
            "advice": {
              "generalPopulation": "Enjoy your outdoor activities.",
              "sensitivePopulation": "Enjoy your outdoor activities."
            }
          }
        },
        {
          "code": "gb-defra",
          "name": "DAQI (GB)",
          "aqi": 3,
          "aqiDisplay": "3",
          "level": "1",
          "category": "Low",
          "color": {
            "red": 49,
            "green": 207,
            "blue": 0,
            "alpha": 1
          },
          "primaryPollutant": {
            "code": "o3",
            "name": "O3",
            "fullName": "Ozone"
          },
          "health": {
            "effect": null,
            "advice": {
              "generalPopulation": "Enjoy your usual outdoor activities.",
              "sensitivePopulation": "Enjoy your usual outdoor activities."
            }
          }
        }
      ],
      "pollutants": [
        {
          "code": "pm2p5",
          "name": "PM 2.5",
          "fullName": "Fine particulate matter (<2.5µm)",
          "concentration": {
            "value": 5.63,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.5,
              "aqiDisplay": "0.5"
            }
          ]
        },
        {
          "code": "pm10",
          "name": "PM 10",
          "fullName": "Inhalable particulate matter (<10µm)",
          "concentration": {
            "value": 2.88,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.2,
              "aqiDisplay": "0.2"
            }
          ]
        },
        {
          "code": "no2",
          "name": "NO2",
          "fullName": "Nitrogen dioxide",
          "concentration": {
            "value": 1.81,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.1,
              "aqiDisplay": "0.1"
            }
          ]
        },
        {
          "code": "o3",
          "name": "O3",
          "fullName": "Ozone",
          "concentration": {
            "value": 74.07,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 1.5,
              "aqiDisplay": "1.5"
            }
          ]
        },
        {
          "code": "so2",
          "name": "SO2",
          "fullName": "Sulfur dioxide",
          "concentration": {
            "value": 9.82,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.2,
              "aqiDisplay": "0.2"
            }
          ]
        }
      ]
    },
    {
      "forecastTime": "2023-05-17T15:00Z",
      "indexes": [
        {
          "code": "qaqi",
          "name": "QAQI",
          "aqi": 1.6,
          "aqiDisplay": "1.6",
          "level": "1",
          "category": "Excellent",
          "color": {
            "red": 195,
            "green": 217,
            "blue": 78,
            "alpha": 1
          },
          "primaryPollutant": {
            "code": "o3",
            "name": "O3",
            "fullName": "Ozone"
          },
          "health": {
            "effect": "No health implications.",
            "advice": {
              "generalPopulation": "Enjoy your outdoor activities.",
              "sensitivePopulation": "Enjoy your outdoor activities."
            }
          }
        },
        {
          "code": "gb-defra",
          "name": "DAQI (GB)",
          "aqi": 3,
          "aqiDisplay": "3",
          "level": "1",
          "category": "Low",
          "color": {
            "red": 49,
            "green": 207,
            "blue": 0,
            "alpha": 1
          },
          "primaryPollutant": {
            "code": "o3",
            "name": "O3",
            "fullName": "Ozone"
          },
          "health": {
            "effect": null,
            "advice": {
              "generalPopulation": "Enjoy your usual outdoor activities.",
              "sensitivePopulation": "Enjoy your usual outdoor activities."
            }
          }
        }
      ],
      "pollutants": [
        {
          "code": "pm2p5",
          "name": "PM 2.5",
          "fullName": "Fine particulate matter (<2.5µm)",
          "concentration": {
            "value": 5.49,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.5,
              "aqiDisplay": "0.5"
            }
          ]
        },
        {
          "code": "pm10",
          "name": "PM 10",
          "fullName": "Inhalable particulate matter (<10µm)",
          "concentration": {
            "value": 2.75,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.2,
              "aqiDisplay": "0.2"
            }
          ]
        },
        {
          "code": "no2",
          "name": "NO2",
          "fullName": "Nitrogen dioxide",
          "concentration": {
            "value": 1.67,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.1,
              "aqiDisplay": "0.1"
            }
          ]
        },
        {
          "code": "o3",
          "name": "O3",
          "fullName": "Ozone",
          "concentration": {
            "value": 76.61,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 1.6,
              "aqiDisplay": "1.6"
            }
          ]
        },
        {
          "code": "so2",
          "name": "SO2",
          "fullName": "Sulfur dioxide",
          "concentration": {
            "value": 9.27,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.2,
              "aqiDisplay": "0.2"
            }
          ]
        }
      ]
    },
    {
      "forecastTime": "2023-05-17T16:00Z",
      "indexes": [
        {
          "code": "qaqi",
          "name": "QAQI",
          "aqi": 1.6,
          "aqiDisplay": "1.6",
          "level": "1",
          "category": "Excellent",
          "color": {
            "red": 195,
            "green": 217,
            "blue": 78,
            "alpha": 1
          },
          "primaryPollutant": {
            "code": "o3",
            "name": "O3",
            "fullName": "Ozone"
          },
          "health": {
            "effect": "No health implications.",
            "advice": {
              "generalPopulation": "Enjoy your outdoor activities.",
              "sensitivePopulation": "Enjoy your outdoor activities."
            }
          }
        },
        {
          "code": "gb-defra",
          "name": "DAQI (GB)",
          "aqi": 3,
          "aqiDisplay": "3",
          "level": "1",
          "category": "Low",
          "color": {
            "red": 49,
            "green": 207,
            "blue": 0,
            "alpha": 1
          },
          "primaryPollutant": {
            "code": "o3",
            "name": "O3",
            "fullName": "Ozone"
          },
          "health": {
            "effect": null,
            "advice": {
              "generalPopulation": "Enjoy your usual outdoor activities.",
              "sensitivePopulation": "Enjoy your usual outdoor activities."
            }
          }
        }
      ],
      "pollutants": [
        {
          "code": "pm2p5",
          "name": "PM 2.5",
          "fullName": "Fine particulate matter (<2.5µm)",
          "concentration": {
            "value": 5.31,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.5,
              "aqiDisplay": "0.5"
            }
          ]
        },
        {
          "code": "pm10",
          "name": "PM 10",
          "fullName": "Inhalable particulate matter (<10µm)",
          "concentration": {
            "value": 2.55,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.2,
              "aqiDisplay": "0.2"
            }
          ]
        },
        {
          "code": "no2",
          "name": "NO2",
          "fullName": "Nitrogen dioxide",
          "concentration": {
            "value": 1.6,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.1,
              "aqiDisplay": "0.1"
            }
          ]
        },
        {
          "code": "o3",
          "name": "O3",
          "fullName": "Ozone",
          "concentration": {
            "value": 78.22,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 1.6,
              "aqiDisplay": "1.6"
            }
          ]
        },
        {
          "code": "so2",
          "name": "SO2",
          "fullName": "Sulfur dioxide",
          "concentration": {
            "value": 10.08,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.3,
              "aqiDisplay": "0.3"
            }
          ]
        }
      ]
    },
    {
      "forecastTime": "2023-05-17T17:00Z",
      "indexes": [
        {
          "code": "qaqi",
          "name": "QAQI",
          "aqi": 1.5,
          "aqiDisplay": "1.5",
          "level": "1",
          "category": "Excellent",
          "color": {
            "red": 195,
            "green": 217,
            "blue": 78,
            "alpha": 1
          },
          "primaryPollutant": {
            "code": "o3",
            "name": "O3",
            "fullName": "Ozone"
          },
          "health": {
            "effect": "No health implications.",
            "advice": {
              "generalPopulation": "Enjoy your outdoor activities.",
              "sensitivePopulation": "Enjoy your outdoor activities."
            }
          }
        },
        {
          "code": "gb-defra",
          "name": "DAQI (GB)",
          "aqi": 3,
          "aqiDisplay": "3",
          "level": "1",
          "category": "Low",
          "color": {
            "red": 49,
            "green": 207,
            "blue": 0,
            "alpha": 1
          },
          "primaryPollutant": {
            "code": "o3",
            "name": "O3",
            "fullName": "Ozone"
          },
          "health": {
            "effect": null,
            "advice": {
              "generalPopulation": "Enjoy your usual outdoor activities.",
              "sensitivePopulation": "Enjoy your usual outdoor activities."
            }
          }
        }
      ],
      "pollutants": [
        {
          "code": "pm2p5",
          "name": "PM 2.5",
          "fullName": "Fine particulate matter (<2.5µm)",
          "concentration": {
            "value": 5.68,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.5,
              "aqiDisplay": "0.5"
            }
          ]
        },
        {
          "code": "pm10",
          "name": "PM 10",
          "fullName": "Inhalable particulate matter (<10µm)",
          "concentration": {
            "value": 2.69,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.2,
              "aqiDisplay": "0.2"
            }
          ]
        },
        {
          "code": "no2",
          "name": "NO2",
          "fullName": "Nitrogen dioxide",
          "concentration": {
            "value": 1.69,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.1,
              "aqiDisplay": "0.1"
            }
          ]
        },
        {
          "code": "o3",
          "name": "O3",
          "fullName": "Ozone",
          "concentration": {
            "value": 74.84,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 1.5,
              "aqiDisplay": "1.5"
            }
          ]
        },
        {
          "code": "so2",
          "name": "SO2",
          "fullName": "Sulfur dioxide",
          "concentration": {
            "value": 12.51,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.3,
              "aqiDisplay": "0.3"
            }
          ]
        }
      ]
    },
    {
      "forecastTime": "2023-05-17T18:00Z",
      "indexes": [
        {
          "code": "qaqi",
          "name": "QAQI",
          "aqi": 1.5,
          "aqiDisplay": "1.5",
          "level": "1",
          "category": "Excellent",
          "color": {
            "red": 195,
            "green": 217,
            "blue": 78,
            "alpha": 1
          },
          "primaryPollutant": {
            "code": "o3",
            "name": "O3",
            "fullName": "Ozone"
          },
          "health": {
            "effect": "No health implications.",
            "advice": {
              "generalPopulation": "Enjoy your outdoor activities.",
              "sensitivePopulation": "Enjoy your outdoor activities."
            }
          }
        },
        {
          "code": "gb-defra",
          "name": "DAQI (GB)",
          "aqi": 3,
          "aqiDisplay": "3",
          "level": "1",
          "category": "Low",
          "color": {
            "red": 49,
            "green": 207,
            "blue": 0,
            "alpha": 1
          },
          "primaryPollutant": {
            "code": "o3",
            "name": "O3",
            "fullName": "Ozone"
          },
          "health": {
            "effect": null,
            "advice": {
              "generalPopulation": "Enjoy your usual outdoor activities.",
              "sensitivePopulation": "Enjoy your usual outdoor activities."
            }
          }
        }
      ],
      "pollutants": [
        {
          "code": "pm2p5",
          "name": "PM 2.5",
          "fullName": "Fine particulate matter (<2.5µm)",
          "concentration": {
            "value": 6.61,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.6,
              "aqiDisplay": "0.6"
            }
          ]
        },
        {
          "code": "pm10",
          "name": "PM 10",
          "fullName": "Inhalable particulate matter (<10µm)",
          "concentration": {
            "value": 2.75,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.2,
              "aqiDisplay": "0.2"
            }
          ]
        },
        {
          "code": "no2",
          "name": "NO2",
          "fullName": "Nitrogen dioxide",
          "concentration": {
            "value": 2.24,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.1,
              "aqiDisplay": "0.1"
            }
          ]
        },
        {
          "code": "o3",
          "name": "O3",
          "fullName": "Ozone",
          "concentration": {
            "value": 70.96,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 1.5,
              "aqiDisplay": "1.5"
            }
          ]
        },
        {
          "code": "so2",
          "name": "SO2",
          "fullName": "Sulfur dioxide",
          "concentration": {
            "value": 16.28,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.4,
              "aqiDisplay": "0.4"
            }
          ]
        }
      ]
    },
    {
      "forecastTime": "2023-05-17T19:00Z",
      "indexes": [
        {
          "code": "qaqi",
          "name": "QAQI",
          "aqi": 1.3,
          "aqiDisplay": "1.3",
          "level": "1",
          "category": "Excellent",
          "color": {
            "red": 195,
            "green": 217,
            "blue": 78,
            "alpha": 1
          },
          "primaryPollutant": {
            "code": "o3",
            "name": "O3",
            "fullName": "Ozone"
          },
          "health": {
            "effect": "No health implications.",
            "advice": {
              "generalPopulation": "Enjoy your outdoor activities.",
              "sensitivePopulation": "Enjoy your outdoor activities."
            }
          }
        },
        {
          "code": "gb-defra",
          "name": "DAQI (GB)",
          "aqi": 2,
          "aqiDisplay": "2",
          "level": "1",
          "category": "Low",
          "color": {
            "red": 49,
            "green": 255,
            "blue": 0,
            "alpha": 1
          },
          "primaryPollutant": {
            "code": "o3",
            "name": "O3",
            "fullName": "Ozone"
          },
          "health": {
            "effect": null,
            "advice": {
              "generalPopulation": "Enjoy your usual outdoor activities.",
              "sensitivePopulation": "Enjoy your usual outdoor activities."
            }
          }
        }
      ],
      "pollutants": [
        {
          "code": "pm2p5",
          "name": "PM 2.5",
          "fullName": "Fine particulate matter (<2.5µm)",
          "concentration": {
            "value": 6.9,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.6,
              "aqiDisplay": "0.6"
            }
          ]
        },
        {
          "code": "pm10",
          "name": "PM 10",
          "fullName": "Inhalable particulate matter (<10µm)",
          "concentration": {
            "value": 3.12,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.2,
              "aqiDisplay": "0.2"
            }
          ]
        },
        {
          "code": "no2",
          "name": "NO2",
          "fullName": "Nitrogen dioxide",
          "concentration": {
            "value": 3.02,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.1,
              "aqiDisplay": "0.1"
            }
          ]
        },
        {
          "code": "o3",
          "name": "O3",
          "fullName": "Ozone",
          "concentration": {
            "value": 61.81,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 1.3,
              "aqiDisplay": "1.3"
            }
          ]
        },
        {
          "code": "so2",
          "name": "SO2",
          "fullName": "Sulfur dioxide",
          "concentration": {
            "value": 23.05,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.5,
              "aqiDisplay": "0.5"
            }
          ]
        }
      ]
    },
    {
      "forecastTime": "2023-05-17T20:00Z",
      "indexes": [
        {
          "code": "qaqi",
          "name": "QAQI",
          "aqi": 1.0,
          "aqiDisplay": "1.0",
          "level": "1",
          "category": "Excellent",
          "color": {
            "red": 195,
            "green": 217,
            "blue": 78,
            "alpha": 1
          },
          "primaryPollutant": {
            "code": "o3",
            "name": "O3",
            "fullName": "Ozone"
          },
          "health": {
            "effect": "No health implications.",
            "advice": {
              "generalPopulation": "Enjoy your outdoor activities.",
              "sensitivePopulation": "Enjoy your outdoor activities."
            }
          }
        },
        {
          "code": "gb-defra",
          "name": "DAQI (GB)",
          "aqi": 2,
          "aqiDisplay": "2",
          "level": "1",
          "category": "Low",
          "color": {
            "red": 49,
            "green": 255,
            "blue": 0,
            "alpha": 1
          },
          "primaryPollutant": {
            "code": "o3",
            "name": "O3",
            "fullName": "Ozone"
          },
          "health": {
            "effect": null,
            "advice": {
              "generalPopulation": "Enjoy your usual outdoor activities.",
              "sensitivePopulation": "Enjoy your usual outdoor activities."
            }
          }
        }
      ],
      "pollutants": [
        {
          "code": "pm2p5",
          "name": "PM 2.5",
          "fullName": "Fine particulate matter (<2.5µm)",
          "concentration": {
            "value": 8.04,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.7,
              "aqiDisplay": "0.7"
            }
          ]
        },
        {
          "code": "pm10",
          "name": "PM 10",
          "fullName": "Inhalable particulate matter (<10µm)",
          "concentration": {
            "value": 3.21,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.2,
              "aqiDisplay": "0.2"
            }
          ]
        },
        {
          "code": "no2",
          "name": "NO2",
          "fullName": "Nitrogen dioxide",
          "concentration": {
            "value": 3.71,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.1,
              "aqiDisplay": "0.1"
            }
          ]
        },
        {
          "code": "o3",
          "name": "O3",
          "fullName": "Ozone",
          "concentration": {
            "value": 46.33,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 1,
              "aqiDisplay": "1"
            }
          ]
        },
        {
          "code": "so2",
          "name": "SO2",
          "fullName": "Sulfur dioxide",
          "concentration": {
            "value": 36.13,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.8,
              "aqiDisplay": "0.8"
            }
          ]
        }
      ]
    },
    {
      "forecastTime": "2023-05-17T21:00Z",
      "indexes": [
        {
          "code": "qaqi",
          "name": "QAQI",
          "aqi": 0.9,
          "aqiDisplay": "0.9",
          "level": "1",
          "category": "Excellent",
          "color": {
            "red": 195,
            "green": 217,
            "blue": 78,
            "alpha": 1
          },
          "primaryPollutant": {
            "code": "so2",
            "name": "SO2",
            "fullName": "Sulfur dioxide"
          },
          "health": {
            "effect": "No health implications.",
            "advice": {
              "generalPopulation": "Enjoy your outdoor activities.",
              "sensitivePopulation": "Enjoy your outdoor activities."
            }
          }
        },
        {
          "code": "gb-defra",
          "name": "DAQI (GB)",
          "aqi": 1,
          "aqiDisplay": "1",
          "level": "1",
          "category": "Low",
          "color": {
            "red": 156,
            "green": 255,
            "blue": 156,
            "alpha": 1
          },
          "primaryPollutant": {
            "code": "so2",
            "name": "SO2",
            "fullName": "Sulfur dioxide"
          },
          "health": {
            "effect": null,
            "advice": {
              "generalPopulation": "Enjoy your usual outdoor activities.",
              "sensitivePopulation": "Enjoy your usual outdoor activities."
            }
          }
        }
      ],
      "pollutants": [
        {
          "code": "pm2p5",
          "name": "PM 2.5",
          "fullName": "Fine particulate matter (<2.5µm)",
          "concentration": {
            "value": 9.4,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.8,
              "aqiDisplay": "0.8"
            }
          ]
        },
        {
          "code": "pm10",
          "name": "PM 10",
          "fullName": "Inhalable particulate matter (<10µm)",
          "concentration": {
            "value": 3.2,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.2,
              "aqiDisplay": "0.2"
            }
          ]
        },
        {
          "code": "no2",
          "name": "NO2",
          "fullName": "Nitrogen dioxide",
          "concentration": {
            "value": 4.43,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.1,
              "aqiDisplay": "0.1"
            }
          ]
        },
        {
          "code": "o3",
          "name": "O3",
          "fullName": "Ozone",
          "concentration": {
            "value": 32.22,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.7,
              "aqiDisplay": "0.7"
            }
          ]
        },
        {
          "code": "so2",
          "name": "SO2",
          "fullName": "Sulfur dioxide",
          "concentration": {
            "value": 40.73,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.9,
              "aqiDisplay": "0.9"
            }
          ]
        }
      ]
    },
    {
      "forecastTime": "2023-05-17T22:00Z",
      "indexes": [
        {
          "code": "qaqi",
          "name": "QAQI",
          "aqi": 1.1,
          "aqiDisplay": "1.1",
          "level": "1",
          "category": "Excellent",
          "color": {
            "red": 195,
            "green": 217,
            "blue": 78,
            "alpha": 1
          },
          "primaryPollutant": {
            "code": "pm2p5",
            "name": "PM 2.5",
            "fullName": "Fine particulate matter (<2.5µm)"
          },
          "health": {
            "effect": "No health implications.",
            "advice": {
              "generalPopulation": "Enjoy your outdoor activities.",
              "sensitivePopulation": "Enjoy your outdoor activities."
            }
          }
        },
        {
          "code": "gb-defra",
          "name": "DAQI (GB)",
          "aqi": 2,
          "aqiDisplay": "2",
          "level": "1",
          "category": "Low",
          "color": {
            "red": 49,
            "green": 255,
            "blue": 0,
            "alpha": 1
          },
          "primaryPollutant": {
            "code": "pm2p5",
            "name": "PM 2.5",
            "fullName": "Fine particulate matter (<2.5µm)"
          },
          "health": {
            "effect": null,
            "advice": {
              "generalPopulation": "Enjoy your usual outdoor activities.",
              "sensitivePopulation": "Enjoy your usual outdoor activities."
            }
          }
        }
      ],
      "pollutants": [
        {
          "code": "pm2p5",
          "name": "PM 2.5",
          "fullName": "Fine particulate matter (<2.5µm)",
          "concentration": {
            "value": 13.16,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 1.1,
              "aqiDisplay": "1.1"
            }
          ]
        },
        {
          "code": "pm10",
          "name": "PM 10",
          "fullName": "Inhalable particulate matter (<10µm)",
          "concentration": {
            "value": 2.97,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.2,
              "aqiDisplay": "0.2"
            }
          ]
        },
        {
          "code": "no2",
          "name": "NO2",
          "fullName": "Nitrogen dioxide",
          "concentration": {
            "value": 4.01,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.1,
              "aqiDisplay": "0.1"
            }
          ]
        },
        {
          "code": "o3",
          "name": "O3",
          "fullName": "Ozone",
          "concentration": {
            "value": 30.26,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.7,
              "aqiDisplay": "0.7"
            }
          ]
        },
        {
          "code": "so2",
          "name": "SO2",
          "fullName": "Sulfur dioxide",
          "concentration": {
            "value": 39.04,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.8,
              "aqiDisplay": "0.8"
            }
          ]
        }
      ]
    },
    {
      "forecastTime": "2023-05-17T23:00Z",
      "indexes": [
        {
          "code": "qaqi",
          "name": "QAQI",
          "aqi": 1.2,
          "aqiDisplay": "1.2",
          "level": "1",
          "category": "Excellent",
          "color": {
            "red": 195,
            "green": 217,
            "blue": 78,
            "alpha": 1
          },
          "primaryPollutant": {
            "code": "pm2p5",
            "name": "PM 2.5",
            "fullName": "Fine particulate matter (<2.5µm)"
          },
          "health": {
            "effect": "No health implications.",
            "advice": {
              "generalPopulation": "Enjoy your outdoor activities.",
              "sensitivePopulation": "Enjoy your outdoor activities."
            }
          }
        },
        {
          "code": "gb-defra",
          "name": "DAQI (GB)",
          "aqi": 2,
          "aqiDisplay": "2",
          "level": "1",
          "category": "Low",
          "color": {
            "red": 49,
            "green": 255,
            "blue": 0,
            "alpha": 1
          },
          "primaryPollutant": {
            "code": "pm2p5",
            "name": "PM 2.5",
            "fullName": "Fine particulate matter (<2.5µm)"
          },
          "health": {
            "effect": null,
            "advice": {
              "generalPopulation": "Enjoy your usual outdoor activities.",
              "sensitivePopulation": "Enjoy your usual outdoor activities."
            }
          }
        }
      ],
      "pollutants": [
        {
          "code": "pm2p5",
          "name": "PM 2.5",
          "fullName": "Fine particulate matter (<2.5µm)",
          "concentration": {
            "value": 14.66,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 1.2,
              "aqiDisplay": "1.2"
            }
          ]
        },
        {
          "code": "pm10",
          "name": "PM 10",
          "fullName": "Inhalable particulate matter (<10µm)",
          "concentration": {
            "value": 2.47,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.1,
              "aqiDisplay": "0.1"
            }
          ]
        },
        {
          "code": "no2",
          "name": "NO2",
          "fullName": "Nitrogen dioxide",
          "concentration": {
            "value": 3.79,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.1,
              "aqiDisplay": "0.1"
            }
          ]
        },
        {
          "code": "o3",
          "name": "O3",
          "fullName": "Ozone",
          "concentration": {
            "value": 31.4,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.7,
              "aqiDisplay": "0.7"
            }
          ]
        },
        {
          "code": "so2",
          "name": "SO2",
          "fullName": "Sulfur dioxide",
          "concentration": {
            "value": 38.28,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.8,
              "aqiDisplay": "0.8"
            }
          ]
        }
      ]
    },
    {
      "forecastTime": "2023-05-18T00:00Z",
      "indexes": [
        {
          "code": "qaqi",
          "name": "QAQI",
          "aqi": 1.2,
          "aqiDisplay": "1.2",
          "level": "1",
          "category": "Excellent",
          "color": {
            "red": 195,
            "green": 217,
            "blue": 78,
            "alpha": 1
          },
          "primaryPollutant": {
            "code": "pm2p5",
            "name": "PM 2.5",
            "fullName": "Fine particulate matter (<2.5µm)"
          },
          "health": {
            "effect": "No health implications.",
            "advice": {
              "generalPopulation": "Enjoy your outdoor activities.",
              "sensitivePopulation": "Enjoy your outdoor activities."
            }
          }
        },
        {
          "code": "gb-defra",
          "name": "DAQI (GB)",
          "aqi": 2,
          "aqiDisplay": "2",
          "level": "1",
          "category": "Low",
          "color": {
            "red": 49,
            "green": 255,
            "blue": 0,
            "alpha": 1
          },
          "primaryPollutant": {
            "code": "pm2p5",
            "name": "PM 2.5",
            "fullName": "Fine particulate matter (<2.5µm)"
          },
          "health": {
            "effect": null,
            "advice": {
              "generalPopulation": "Enjoy your usual outdoor activities.",
              "sensitivePopulation": "Enjoy your usual outdoor activities."
            }
          }
        }
      ],
      "pollutants": [
        {
          "code": "pm2p5",
          "name": "PM 2.5",
          "fullName": "Fine particulate matter (<2.5µm)",
          "concentration": {
            "value": 14.71,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 1.2,
              "aqiDisplay": "1.2"
            }
          ]
        },
        {
          "code": "pm10",
          "name": "PM 10",
          "fullName": "Inhalable particulate matter (<10µm)",
          "concentration": {
            "value": 2.29,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.1,
              "aqiDisplay": "0.1"
            }
          ]
        },
        {
          "code": "no2",
          "name": "NO2",
          "fullName": "Nitrogen dioxide",
          "concentration": {
            "value": 3.23,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.1,
              "aqiDisplay": "0.1"
            }
          ]
        },
        {
          "code": "o3",
          "name": "O3",
          "fullName": "Ozone",
          "concentration": {
            "value": 30.46,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.7,
              "aqiDisplay": "0.7"
            }
          ]
        },
        {
          "code": "so2",
          "name": "SO2",
          "fullName": "Sulfur dioxide",
          "concentration": {
            "value": 38.07,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.8,
              "aqiDisplay": "0.8"
            }
          ]
        }
      ]
    },
    {
      "forecastTime": "2023-05-18T01:00Z",
      "indexes": [
        {
          "code": "qaqi",
          "name": "QAQI",
          "aqi": 1.2,
          "aqiDisplay": "1.2",
          "level": "1",
          "category": "Excellent",
          "color": {
            "red": 195,
            "green": 217,
            "blue": 78,
            "alpha": 1
          },
          "primaryPollutant": {
            "code": "pm2p5",
            "name": "PM 2.5",
            "fullName": "Fine particulate matter (<2.5µm)"
          },
          "health": {
            "effect": "No health implications.",
            "advice": {
              "generalPopulation": "Enjoy your outdoor activities.",
              "sensitivePopulation": "Enjoy your outdoor activities."
            }
          }
        },
        {
          "code": "gb-defra",
          "name": "DAQI (GB)",
          "aqi": 2,
          "aqiDisplay": "2",
          "level": "1",
          "category": "Low",
          "color": {
            "red": 49,
            "green": 255,
            "blue": 0,
            "alpha": 1
          },
          "primaryPollutant": {
            "code": "pm2p5",
            "name": "PM 2.5",
            "fullName": "Fine particulate matter (<2.5µm)"
          },
          "health": {
            "effect": null,
            "advice": {
              "generalPopulation": "Enjoy your usual outdoor activities.",
              "sensitivePopulation": "Enjoy your usual outdoor activities."
            }
          }
        }
      ],
      "pollutants": [
        {
          "code": "pm2p5",
          "name": "PM 2.5",
          "fullName": "Fine particulate matter (<2.5µm)",
          "concentration": {
            "value": 14.7,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 1.2,
              "aqiDisplay": "1.2"
            }
          ]
        },
        {
          "code": "pm10",
          "name": "PM 10",
          "fullName": "Inhalable particulate matter (<10µm)",
          "concentration": {
            "value": 2.16,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.1,
              "aqiDisplay": "0.1"
            }
          ]
        },
        {
          "code": "no2",
          "name": "NO2",
          "fullName": "Nitrogen dioxide",
          "concentration": {
            "value": 2.76,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.1,
              "aqiDisplay": "0.1"
            }
          ]
        },
        {
          "code": "o3",
          "name": "O3",
          "fullName": "Ozone",
          "concentration": {
            "value": 29.04,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.6,
              "aqiDisplay": "0.6"
            }
          ]
        },
        {
          "code": "so2",
          "name": "SO2",
          "fullName": "Sulfur dioxide",
          "concentration": {
            "value": 35.58,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.8,
              "aqiDisplay": "0.8"
            }
          ]
        }
      ]
    },
    {
      "forecastTime": "2023-05-18T02:00Z",
      "indexes": [
        {
          "code": "qaqi",
          "name": "QAQI",
          "aqi": 1.2,
          "aqiDisplay": "1.2",
          "level": "1",
          "category": "Excellent",
          "color": {
            "red": 195,
            "green": 217,
            "blue": 78,
            "alpha": 1
          },
          "primaryPollutant": {
            "code": "pm2p5",
            "name": "PM 2.5",
            "fullName": "Fine particulate matter (<2.5µm)"
          },
          "health": {
            "effect": "No health implications.",
            "advice": {
              "generalPopulation": "Enjoy your outdoor activities.",
              "sensitivePopulation": "Enjoy your outdoor activities."
            }
          }
        },
        {
          "code": "gb-defra",
          "name": "DAQI (GB)",
          "aqi": 2,
          "aqiDisplay": "2",
          "level": "1",
          "category": "Low",
          "color": {
            "red": 49,
            "green": 255,
            "blue": 0,
            "alpha": 1
          },
          "primaryPollutant": {
            "code": "pm2p5",
            "name": "PM 2.5",
            "fullName": "Fine particulate matter (<2.5µm)"
          },
          "health": {
            "effect": null,
            "advice": {
              "generalPopulation": "Enjoy your usual outdoor activities.",
              "sensitivePopulation": "Enjoy your usual outdoor activities."
            }
          }
        }
      ],
      "pollutants": [
        {
          "code": "pm2p5",
          "name": "PM 2.5",
          "fullName": "Fine particulate matter (<2.5µm)",
          "concentration": {
            "value": 14.18,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 1.2,
              "aqiDisplay": "1.2"
            }
          ]
        },
        {
          "code": "pm10",
          "name": "PM 10",
          "fullName": "Inhalable particulate matter (<10µm)",
          "concentration": {
            "value": 1.95,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.1,
              "aqiDisplay": "0.1"
            }
          ]
        },
        {
          "code": "no2",
          "name": "NO2",
          "fullName": "Nitrogen dioxide",
          "concentration": {
            "value": 2.3,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.1,
              "aqiDisplay": "0.1"
            }
          ]
        },
        {
          "code": "o3",
          "name": "O3",
          "fullName": "Ozone",
          "concentration": {
            "value": 33.35,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.7,
              "aqiDisplay": "0.7"
            }
          ]
        },
        {
          "code": "so2",
          "name": "SO2",
          "fullName": "Sulfur dioxide",
          "concentration": {
            "value": 30.77,
            "unit": "μg/m3"
          },
          "subIndexes": [
            {
              "code": "qaqi",
              "aqi": 0.7,
              "aqiDisplay": "0.7"
            }
          ]
        }
      ]
    }
  ]
}
```

*   metadata object  [元数据](https://dev.qweather.com/docs/resource/metadata/)  
    *   tag string  数据唯一标识  
    *   attributions array  数据归因信息或声明，必须与当前数据共同显示  

*   hours array  每小时空气质量数据列表  
    *   forecastTime date-time  预报时间  
    *   indexes array  空气质量指数列表，参考[支持的空气质量指数](https://dev.qweather.com/docs/api/air-quality/aqi-list/)  
        *   code string  空气质量指数的code  
        *   name string  空气质量指数的名字  
        *   aqi number  空气质量指数的数值，包括将非数字形式的指数值转换为数字后的结果，便于程序计算  
        *   aqiDisplay string  空气质量指数的标准显示值，可能包含非数字字符，向用户展示时建议使用该字段  
        *   level string  空气质量指数等级  
        *   category string  空气质量指数类别  
        *   color object  空气质量指数的 RGBA 颜色表示  
            *   red number  红色分量值，取值范围 [0, 255]  
            *   green number  绿色分量值，取值范围 [0, 255]  
            *   blue number  蓝色分量值，取值范围 [0, 255]  
            *   alpha number  透明度分量值，取值范围 [0, 1]  

        *   primaryPollutant object  首要[污染物](https://dev.qweather.com/docs/api/air-quality/pollutant-list/#primary-pollutant)表示导致当前空气污染的主要成分  
            *   code string  首要污染物的code  
            *   name string  首要污染物的名字  
            *   fullName string  首要污染物的全称  

        *   health object  空气质量对一般人群和敏感人群的[健康影响及指导建议](https://dev.qweather.com/docs/api/air-quality/health-effect-advice/)  
            *   effect string  空气质量对健康的影响，可能为空  
            *   advice object  健康建议，可能为空  
                *   generalPopulation string  对一般人群的健康指导意见，可能为空  
                *   sensitivePopulation string  对敏感人群的健康指导意见，可能为空  

    *   pollutants array  [污染物](https://dev.qweather.com/docs/api/air-quality/pollutant-list/)列表  
        *   code string  污染物code，可选值：`pm10`、`pm2p5`、`co`、`no`、`no2`、`so2`、`o3`、`nmhc`  
        *   name string  污染物的名字  
        *   fullName string  污染物的全称  
        *   concentration object  污染物的浓度值  
            *   value number  数值  
            *   unit string  [单位](https://dev.qweather.com/docs/resource/unit/)  

        *   subIndexes array  各项污染物的空气质量分指数列表，通常最差的污染物分指数代表当前的空气质量指数，并用于确定首要污染物  
            *   code string  分指数对应的空气质量指数代码  
            *   aqi number  分指数的数值  
            *   aqiDisplay string  分指数数值的显示名称  

#### 本页导航

*   [请求路径](https://dev.qweather.com/docs/api/air-quality/air-hourly-forecast#endpoint)
*   [参数](https://dev.qweather.com/docs/api/air-quality/air-hourly-forecast#parameters)
*   [请求示例](https://dev.qweather.com/docs/api/air-quality/air-hourly-forecast#request-example)
*   [返回数据](https://dev.qweather.com/docs/api/air-quality/air-hourly-forecast#response)

### [和风天气开发者服务](https://dev.qweather.com/)

[联系](https://www.qweather.com/contact/)• [博客](https://blog.qweather.com/)• [GitHub](https://github.com/qwd)

Language 

### 文档

*   [开始使用](https://dev.qweather.com/docs/start/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [开发文档](https://dev.qweather.com/docs/api/)
*   [财务与费用](https://dev.qweather.com/docs/finance/)
*   [最佳实践](https://dev.qweather.com/docs/best-practices/)
*   [实用资料](https://dev.qweather.com/docs/resource/)

### 特性

*   [服务和数据](https://dev.qweather.com/docs/features/service-and-data/)
*   [全球部署](https://dev.qweather.com/docs/features/global-deployment/)
*   [性能](https://dev.qweather.com/docs/features/performance/)

### 更多

*   [天气预报](https://www.qweather.com/)
*   [天气地图](https://map.qweather.com/)
*   [下载APP](https://www.qweather.com/app/)

*   © 2026 [QWeather](https://www.qweather.com/en/)
*   [许可协议](https://www.qweather.com/terms/developers-eula/)
*   [隐私政策](https://www.qweather.com/terms/privacy)
*   [免责声明](https://www.qweather.com/terms/disclaimer/)

[京ICP备15048401号-11](https://beian.miit.gov.cn/)

<!-- END_DOC_42 -->

---


<!-- START_DOC_43: api_console.md (URL: https://dev.qweather.com/docs/api/console) -->
Title: 控制台API

URL Source: https://dev.qweather.com/docs/api/console

Markdown Content:
[和风天气开发者服务](https://dev.qweather.com/)

[控制台](https://id.qweather.com/#/login?redirect=https://console.qweather.com)

*   [首页](https://dev.qweather.com/)
*   [文档](https://dev.qweather.com/docs/)
*   [图标](https://icons.qweather.com/)
*   [展示](https://dev.qweather.com/showcase/)
*   [帮助](https://dev.qweather.com/help/)
*   [价格](https://dev.qweather.com/price/)
*   Search K 

*   [文档](https://dev.qweather.com/docs/)
*   >
*   [开发文档](https://dev.qweather.com/docs/api/)
*   >
*   [控制台API](https://dev.qweather.com/docs/api/console/)

#### [开始使用](https://dev.qweather.com/docs/start/)

*   [特性](https://dev.qweather.com/docs/features/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [帐号管理](https://dev.qweather.com/docs/account/)

#### [开发文档](https://dev.qweather.com/docs/api/)

*   [GeoAPI](https://dev.qweather.com/docs/api/geoapi/)
*   [天气预报](https://dev.qweather.com/docs/api/weather/)
*   [分钟预报](https://dev.qweather.com/docs/api/minutely/)
*   [预警](https://dev.qweather.com/docs/api/warning/)
*   [天气指数](https://dev.qweather.com/docs/api/indices/)
*   [空气质量](https://dev.qweather.com/docs/api/air-quality/)
*   [时光机](https://dev.qweather.com/docs/api/time-machine/)
*   [热带气旋（台风）](https://dev.qweather.com/docs/api/tropical-cyclone/)
*   [海洋数据](https://dev.qweather.com/docs/api/ocean/)
*   [太阳辐射](https://dev.qweather.com/docs/api/solar-radiation/)
*   [天文](https://dev.qweather.com/docs/api/astronomy/)
*   [控制台API](https://dev.qweather.com/docs/api/console/)

    *   [GET 财务汇总](https://dev.qweather.com/docs/api/console/finance/)
    *   [GET 请求量统计](https://dev.qweather.com/docs/api/console/stats/)

#### [实用资料](https://dev.qweather.com/docs/resource/)

*   [专用词汇表](https://dev.qweather.com/docs/resource/glossary/)
*   [错误码](https://dev.qweather.com/docs/resource/error-code/)
*   [多语言](https://dev.qweather.com/docs/resource/language/)
*   [常见城市列表](https://dev.qweather.com/docs/resource/location-list/)
*   [元数据](https://dev.qweather.com/docs/resource/metadata/)
*   [单位](https://dev.qweather.com/docs/resource/unit/)

#### [最佳实践](https://dev.qweather.com/docs/best-practices/)

*   [不要假设](https://dev.qweather.com/docs/best-practices/no-assumptions/)
*   [优化请求](https://dev.qweather.com/docs/best-practices/optimize-requests/)
*   [处理Gzip](https://dev.qweather.com/docs/best-practices/gzip/)
*   [缓存你的数据](https://dev.qweather.com/docs/best-practices/cache/)
*   [安全指南](https://dev.qweather.com/docs/best-practices/security-guidelines/)

#### [财务与费用](https://dev.qweather.com/docs/finance/)

*   [计费方式和支付](https://dev.qweather.com/docs/finance/billing-and-payment/)
*   [按量计费定价](https://dev.qweather.com/docs/finance/pricing/)
*   [节省计划](https://dev.qweather.com/docs/finance/savings-plans/)
*   [增值税发票](https://dev.qweather.com/docs/finance/vat-invoice/)

#### [条款](https://dev.qweather.com/docs/terms/)

*   [使用限制](https://dev.qweather.com/docs/terms/restriction/)
*   [注明来源](https://dev.qweather.com/docs/terms/attribution/)
*   [服务条款](https://dev.qweather.com/docs/terms/tos/)

# 控制台API

控制台 API 可以轻松的了解帐号的财物信息和请求量信息，你可以根据这些数据评估使用率或者建立财务预警。

出于安全考虑，所有凭据默认没有权限请求控制台API，你必须在凭据设置中启用控制台 API 才可以请求对应的数据。控制台 API 返回的数据均为上一个小时或更早时候，有可能与控制台网站中显示的不一致，请以 `asOf` 响应的时间为准。

API

[GET 财务汇总](https://dev.qweather.com/docs/api/console/finance/)
查询当前帐号的财务和计费汇总信息。

[GET 请求量统计](https://dev.qweather.com/docs/api/console/stats/)
查询当前帐号最近24小时的API请求量统计。

### [和风天气开发者服务](https://dev.qweather.com/)

[联系](https://www.qweather.com/contact/)• [博客](https://blog.qweather.com/)• [GitHub](https://github.com/qwd)

Language 

### 文档

*   [开始使用](https://dev.qweather.com/docs/start/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [开发文档](https://dev.qweather.com/docs/api/)
*   [财务与费用](https://dev.qweather.com/docs/finance/)
*   [最佳实践](https://dev.qweather.com/docs/best-practices/)
*   [实用资料](https://dev.qweather.com/docs/resource/)

### 特性

*   [服务和数据](https://dev.qweather.com/docs/features/service-and-data/)
*   [全球部署](https://dev.qweather.com/docs/features/global-deployment/)
*   [性能](https://dev.qweather.com/docs/features/performance/)

### 更多

*   [天气预报](https://www.qweather.com/)
*   [天气地图](https://map.qweather.com/)
*   [下载APP](https://www.qweather.com/app/)

*   © 2026 [QWeather](https://www.qweather.com/en/)
*   [许可协议](https://www.qweather.com/terms/developers-eula/)
*   [隐私政策](https://www.qweather.com/terms/privacy)
*   [免责声明](https://www.qweather.com/terms/disclaimer/)

[京ICP备15048401号-11](https://beian.miit.gov.cn/)

<!-- END_DOC_43 -->

---


<!-- START_DOC_44: api_astronomy_sun-guide.md (URL: https://dev.qweather.com/docs/api/astronomy/sun-guide) -->
Title: 了解太阳数据

URL Source: https://dev.qweather.com/docs/api/astronomy/sun-guide

Markdown Content:
太阳看起来每天都会东升西落，但“一天从什么时候开始变亮”、“太阳什么时候最高”，以及“什么时候才算完全黑下来”需要用不同的太阳事件描述。

## 日出日落[#](https://dev.qweather.com/docs/api/astronomy/sun-guide#sunrise-and-sunset)

**日出**是太阳上边缘刚从地平线出现的时刻，而非整个日面离开地平线的时刻。**日落**则是太阳上边缘完全消失在地平线下的时刻。

大气折射会使地平线附近的太阳看起来比实际位置稍高，实际观测还会受海拔、地形、建筑、云雾和大气状况影响，所以 API 时间不等同于肉眼一定能看见太阳的时间。

对于高纬度地区，由于极昼和极夜，太阳可能在整个当地日期内都没有穿越地平线。此时 `sunrise` 和 `sunset` 可能为 `null`。在极昼或极夜的起止日期，也可能只有其中一个事件。例如在朗伊尔城，每年11月份至次年3月份处于极夜期，在此期间查询该城市的日出日落将可能返回空数值。

## 太阳正午和太阳子夜[#](https://dev.qweather.com/docs/api/astronomy/sun-guide#solar-noon-and-midnight)

**太阳正午**是太阳经过观测者子午圈上方、当日高度最高的时刻，也称太阳上中天。

**太阳子夜**是太阳经过子午圈下方、当日高度最低的时刻，也称太阳下中天。

太阳正午和太阳子夜是由观测地点的经度和太阳的视运动决定的，两者大致相隔 12 小时，并且它们出现的时间并不是当地固定的 12:00 或 24:00。

## 曙暮光[#](https://dev.qweather.com/docs/api/astronomy/sun-guide#twilight)

太阳位于地平线下时，其光线仍可通过大气散射照亮天空，这段明暗过渡称为**曙暮光**（也称为**晨昏蒙影**）。根据太阳几何中心低于地平线的角度，可分为三个阶段：

| 阶段 | 太阳中心高度角 | 一般观感 |
| --- | --- | --- |
| 民用曙暮光 | 0° 至 -6° | 地平线和地面物体通常仍可分辨 |
| 航海曙暮光 | -6° 至 -12° | 地平线仍可分辨，较亮星体已经出现 |
| 天文曙暮光 | -12° 至 -18° | 天空已很暗，但仍有微弱的太阳散射光 |

正常情况下，太阳事件按以下顺序发生：

```
天文晨光 → 航海晨光 → 民用晨光 → 日出 → 太阳正午 → 日落 → 民用暮光 → 航海暮光 → 天文暮光
```

晨光字段表示对应晨光阶段的**开始**，暮光字段表示对应暮光阶段的**结束**。例如，早晨从 `civilDawn` 到 `sunrise` 是民用晨光，傍晚从 `sunset` 到 `civilDusk` 是民用暮光。

在高纬度地区，太阳可能不会穿越某个高度角，因此某一类或全部晨昏蒙影字段可能为 `null`。

![Image 1: 曙暮光和太阳高度角示意图](https://dev.qweather.com/assets/images/content/twilights-zh.svg)

_曙暮光示意图。请注意，图中角度并非按比例绘制，仅为更清晰地展示曙暮光的类型。（改编自 [Twilight description full day](https://commons.wikimedia.org/wiki/File:Twilight\_description\_full\_day.svg)，TWCarlson，[CC BY-SA 4.0](https://creativecommons.org/licenses/by-sa/4.0/deed.zh-hans)）_

<!-- END_DOC_44 -->

---


<!-- START_DOC_45: api_astronomy_moon-guide.md (URL: https://dev.qweather.com/docs/api/astronomy/moon-guide) -->
Title: 了解月亮数据

URL Source: https://dev.qweather.com/docs/api/astronomy/moon-guide

Markdown Content:
月亮不是只在夜晚出现，也不会在每个当地日期内各发生一次月升和月落。月球绕地球的运动、观测地点和人为划分的日期边界，使月亮数据比太阳数据更容易出现看似异常的情况。

## 月升月落[#](https://dev.qweather.com/docs/api/astronomy/moon-guide#moonrise-and-moonset)

**月升**是月亮上边缘升过地平线的时刻，**月落**是月亮上边缘沉入地平线的时刻。实际看到月亮的时间还会受地形、建筑、云雾和大气折射影响。

与日出日落相比，月升月落时间的日际变化更大，以下是最常见的几种情况。

#### 为什么月亮在早上升起？

为什么有时候月升的时间是上午9点？月亮不是只在晚上出现吗？你们的数据出错了吧？

_实际上，月亮并非只在晚上出现。_

月球绕地球公转一周大约需要 27.3 天（恒星月），因此它相对恒星每天向东移动约：

```
360° / 27.3天 ≈ 13.2°
```

地球完成一次自转后，还需要再转过这一小段角度，才会让月亮回到前一天相似的天空位置：

```
13.2° / 15° × 60分钟 ≈ 52.8分钟
```

这解释了为什么月升时间平均每天推迟约 50 分钟。但由于月球轨道、地球自转轴倾角和观测纬度等因素，实际差值并不固定。

因此，月亮不仅会在夜晚升起。新月前后，月亮大致与太阳同升同落，虽然白天位于地平线上，但很难用肉眼看见；满月前后，月亮大致在日落时升起，在日出时落下。

> **提示：** 上述计算和数据仅为举例说明，并非严谨计算。实际上每天的月升时间相比前一天可能推迟30-70分钟。

#### 为什么一整天都有月亮？

月球轨道平面与地球赤道平面并不重合。在高纬度地区，月亮有时会连续 24 小时以上位于地平线上，也可能整天都位于地平线下。由于当天没有发生穿越地平线的事件，`moonrise` 和 `moonset` 都可能为 `null`。

![Image 1: 地球和月球轨道](https://dev.qweather.com/assets/images/content/earth-moon-orbit-zh.png)_地球和月球轨道（原始图片 [Earth-Moon-zh-Hant](https://commons.wikimedia.org/wiki/File:Earth-Moon-zh-Hant.PNG), NASA ）_

#### 只有月升或只有月落

由于月升和月落平均每天向后推迟，某次事件可能从一天的 23 点多跨过零点，落入隔天的 0 点多。于是，中间那个当地日期内就没有这一事件。这是正常的日期分割结果，不是数据缺失。

因此，某一天可能只有 `moonrise`、只有 `moonset`，或两者都为 `null`。

> ![Image 2: 北京月升月落时间表](https://dev.qweather.com/assets/images/content/moon-rise-set-beijing-2022.jpg)
> 
> 
> _北京2022年月升月落时间表，空白的地方表示月升或月落时间没有发生在当天24小时内。这并非是月亮惹的祸，而是我们的人为的历法导致的。_

## 月亮上中天和下中天[#](https://dev.qweather.com/docs/api/astronomy/moon-guide#moon-transit-and-underfoot)

由于地球自转，月亮每天会两次经过观测者的子午圈：

**月亮上中天是**月亮经过离观测者天顶较近的子午圈位置。对大多数情况而言，这是月亮当天高度最高的时刻。

**月亮下中天**是月亮经过子午圈的另一侧。对大多数情况而言，此时月亮位于地平线下，并达到当天最低高度。

正常情况下，月亮事件按以下顺序发生：

```
月升 → 上中天 → 月落 → 下中天 → 下一次月升
```

上述顺序仅表示一般情况下各事件的先后关系，并不表示月球的实际运动轨迹。在高纬度地区，月亮可能不经过地平线，但仍可发生上中天或下中天。

## 月相[#](https://dev.qweather.com/docs/api/astronomy/moon-guide#moon-phase)

月相是在地球上看到的月球亮面形状。月球本身不发光，太阳始终照亮月球的一半，我们看见的亮面比例则会随太阳、地球和月球的相对位置而变化。一个完整的月相周期平均约为 29.5 天（朔望月）。

![Image 3: 月相](https://dev.qweather.com/assets/images/content/moon-phases-zh.jpg)

_这张图代表了不同月相对应的太阳、地球和月球的相对位置，此时观察者位于北半球，如果观察者位于南半球时，看到的月相形状是左右倒置的。（改编自 [Moon phases en](https://commons.wikimedia.org/wiki/File:Moon\_phases\_en.jpg), Orion 8, [CC BY-SA 4.0](https://creativecommons.org/licenses/by-sa/4.0/deed.zh-hans)）_

当地球位于太阳和月球之间时，我们看到的月球朝向地球的一面几乎全被照亮，这是满月。当月球大致位于太阳和地球之间时，朝向地球的一面几乎全处于黑暗中，这是新月。月相通常不是地球影子造成的；只有月食发生时，地球影子才会遮挡月球。

#### 主导月相

月相可能在当天24小时内发生转换，例如从蛾眉月转为上弦月，和风天气使用“主导月相”的概念来表示当天的月相，即以当地时间为准，如果当天 24 小时内发生新月、上弦月、满月或下弦月这四种瞬时月相中的任意一种，当天便以该月相作为主导月相；其他情况以当地时间 12:00 的月相作为当天的主导月相。

#### 月相示意图

下方表格代表了不同月相的示例。因为月球轨道较接近黄道而非赤道，故下表南北半球的分界严格来说以黄道为分割。

| 月相 | 北半球 | 南半球 | 平均月升/月落 | 北半球示意 | 南半球示意 |
| --- | --- | --- | --- | --- | --- |
| * **新月（朔月）** * `new-moon` | 肉眼几乎不可见，月球近地面没有被太阳直接照亮 | 肉眼几乎不可见，月球近地面没有被太阳直接照亮 | * 06:00 * 18:00 |  |  |
| * **蛾眉月（娥眉月）** * `waxing-crescent` | 右侧的1–49%可见 | 左侧的1–49%可见 | * 09:00 * 21:00 |  |  |
| * **上弦月** * `first-quarter` | 右侧的50%可见 | 左侧的50%可见 | * 12:00 * 00:00 |  |  |
| * **盈凸月** * `waxing-gibbous` | 右侧的51–99%可见 | 左侧的51–99%可见 | * 15:00 * 03:00 |  |  |
| * **满月（望月）** * `full-moon` | 100% | 100% | * 18:00 * 06:00 |  |  |
| * **亏凸月** * `waning-gibbous` | 左侧的99–51%可见 | 右侧的99–51%可见 | * 21:00 * 09:00 |  |  |
| * **下弦月** * `last-quarter` | 左侧的50%可见 | 右侧的50%可见 | * 00:00 * 12:00 |  |  |
| * **残月** * `waning-crescent` | 左侧的49–1%可见 | 右侧的49–1%可见 | * 03:00 * 15:00 |  |  |

#### 了解更多

*   这篇文章中的视频代表了2022年每小时的月相的变化以及月球相对地球的位置：[Moon Phase and Libration, 2022](https://svs.gsfc.nasa.gov/4955)
*   这个视频代表了月相是如何产生的以及在太空中观看的效果，这有助于你理解月相的概念：[The Moon's Phases as Seen from Space](https://www.eso.org/public/videos/moon_phases-1/)

<!-- END_DOC_45 -->

---


<!-- START_DOC_46: api_weather_weather-daily-forecast.md (URL: https://dev.qweather.com/docs/api/weather/weather-daily-forecast) -->
Title: 每日天气预报

URL Source: https://dev.qweather.com/docs/api/weather/weather-daily-forecast

Markdown Content:
## 每日天气预报

获取指定经纬度位置的每日天气预报，最多10天预报，1公里分辨率，覆盖全球任意地点。

每日预报提供白天 [07:00, 19:00)、晚间 [19:00, 次日07:00) 的预报数据，包括：最高和最低温度、天气现象、最大阵风、最大紫外线指数、降水量和概率、海平面气压、湿度、风向和风速、云量、日出日落、月升月落和月相等。

## 请求路径[#](https://dev.qweather.com/docs/api/weather/weather-daily-forecast#endpoint)

`GET /weather/v1/daily/{latitude}/{longitude}`

## 参数[#](https://dev.qweather.com/docs/api/weather/weather-daily-forecast#parameters)

#### 路径参数

*   latitude 必选

number 
所需位置的纬度。十进制，最多支持小数点后两位。例如 `39.92` 
*   longitude 必选

number 
所需位置的经度。十进制，最多支持小数点后两位。例如 `116.41` 

#### 查询参数

*   days

integer 
预报天数，支持 `1-10` 天，默认返回 `7` 天 
*   localTime

boolean 是否返回查询地点的本地时间

可选值： `false` UTC时间（默认），`true` 本地时间  
*   lang

string 

## 请求示例[#](https://dev.qweather.com/docs/api/weather/weather-daily-forecast#request-example)

```
curl -X GET --compressed \
-H 'Authorization: Bearer your_token' \
'https://your-api-host/weather/v1/daily/39.92/116.41'
```

请将`your_token`替换为你的[JWT身份认证](https://dev.qweather.com/docs/configuration/authentication/)，将`your_api_host`替换为你的[API Host](https://dev.qweather.com/docs/configuration/api-host/)

[试一下](https://dev.qweather.com/api-explore/#/Weather/getWeatherDaily)

## 返回数据[#](https://dev.qweather.com/docs/api/weather/weather-daily-forecast#response)

*   metadata

object 
    *   tag

string 
数据唯一标识 
    *   attributions

array 
数据归因信息或声明，必须与当前数据共同显示 

*   days

array 
每日天气预报列表 
    *   forecastStartTime

date-time 
预报数据的开始时间 
    *   forecastEndTime

date-time 
预报数据的结束时间(不含) 
    *   astro

object 
太阳和月亮天文事件 
        *   sunrise

date-time 
日出时间 
        *   sunset

date-time 
日落时间 
        *   astronomicalDawn

date-time 
天文晨光的开始时间 
        *   nauticalDawn

date-time 
航海晨光的开始时间 
        *   civilDawn

date-time 
民用晨光的开始时间 
        *   astronomicalDusk

date-time 
天文暮光的结束时间 
        *   nauticalDusk

date-time 
航海暮光的结束时间 
        *   civilDusk

date-time 
民用暮光的结束时间 
        *   solarNoon

date-time 
太阳正午时间 
        *   solarMidnight

date-time 
太阳子夜时间 
        *   moonrise

date-time 
月出时间 
        *   moonset

date-time 
月落时间 
        *   moonTransit

date-time 
月亮上中天时间 
        *   moonUnderfoot

date-time 
月亮下中天时间 
        *   moonPhase

string 当天主导月相，可选值：

`new-moon`(新月)、`waxing-crescent`(蛾眉月)、`first-quarter`(上弦月)、`waxing-gibbous`(盈凸月)、`full-moon`(满月)、`waning-gibbous`(亏凸月)、`last-quarter`(下弦月)、`waning-crescent`(残月)  

    *   temperatureMax

object 
预报时段的最高温度 
        *   value

number 
数值 
        *   unit

string 

    *   temperatureMin

object 
预报时段的最低温度 
        *   value

number 
数值 
        *   unit

string 

    *   temperatureAvg

object 
当日平均温度 
        *   value

number 
数值 
        *   unit

string 

    *   uvIndexMax

number 
当日最大紫外线指数，取值范围 `[0, 15]` 
    *   daytime

object 
白天预报，当地时间 `[07:00, 19:00)` 
        *   forecastStartTime

date-time 
预报数据的开始时间 
        *   forecastEndTime

date-time 
预报数据的结束时间(不含) 
        *   condition

object 
            *   text

string 
天气现象的本地化描述 
            *   code

string 
天气现象代码 

        *   temperatureMax

object 
预报时段的最高温度 
            *   value

number 
数值 
            *   unit

string 

        *   temperatureMin

object 
预报时段的最低温度 
            *   value

number 
数值 
            *   unit

string 

        *   humidity

number 
预报时段的平均相对湿度，取值范围 `[0, 1]` 
        *   wind

object 
风的数据 
            *   direction

object 
                *   degree

number 
风向角度，取值范围 `[0, 359]` 
                *   compass

string 风向的方位代码

可选值: `n`, `nne`, `ne`, `ene`, `e`, `ese`, `se`, `sse`, `s`, `ssw`, `sw`, `wsw`, `w`, `wnw`, `nw`, `nnw`, `none`, `vrb`  

            *   speed

object 
风速 
                *   value

number 
数值 
                *   unit

string 

            *   scale

number 

        *   windGustMax

object 
预报时段的最大阵风风速 
            *   value

number 
数值 
            *   unit

string 

        *   precipitation

object 
降水数据 
            *   amount

object 
当前数据时段内的累计降水量 
                *   value

number 
数值 
                *   unit

string 

            *   type

string 
降水类型代码： `rain`（雨）、`snow`（雪）、`ice`（冰粒或冻雨）、 `mixed`（混合降水）、`none`（无降水）、`unknown`（未知） 
            *   probability

number 
预报降水数据，取值范围 `[0, 1]` 

        *   cloudCover

number 
预报时段的平均总云量，取值范围 `[0, 1]` 

    *   nighttime

object 
夜间预报，当地时间 `[19:00, 次日07:00)` 
        *   forecastStartTime

date-time 
预报数据的开始时间 
        *   forecastEndTime

date-time 
预报数据的结束时间(不含) 
        *   condition

object 
            *   text

string 
天气现象的本地化描述 
            *   code

string 
天气现象代码 

        *   temperatureMax

object 
预报时段的最高温度 
            *   value

number 
数值 
            *   unit

string 

        *   temperatureMin

object 
预报时段的最低温度 
            *   value

number 
数值 
            *   unit

string 

        *   humidity

number 
预报时段的平均相对湿度，取值范围 `[0, 1]` 
        *   wind

object 
风的数据 
            *   direction

object 
                *   degree

number 
风向角度，取值范围 `[0, 359]` 
                *   compass

string 风向的方位代码

可选值: `n`, `nne`, `ne`, `ene`, `e`, `ese`, `se`, `sse`, `s`, `ssw`, `sw`, `wsw`, `w`, `wnw`, `nw`, `nnw`, `none`, `vrb`  

            *   speed

object 
风速 
                *   value

number 
数值 
                *   unit

string 

            *   scale

number 

        *   windGustMax

object 
预报时段的最大阵风风速 
            *   value

number 
数值 
            *   unit

string 

        *   precipitation

object 
降水数据 
            *   amount

object 
当前数据时段内的累计降水量 
                *   value

number 
数值 
                *   unit

string 

            *   type

string 
降水类型代码： `rain`（雨）、`snow`（雪）、`ice`（冰粒或冻雨）、 `mixed`（混合降水）、`none`（无降水）、`unknown`（未知） 
            *   probability

number 
预报降水数据，取值范围 `[0, 1]` 

        *   cloudCover

number 
预报时段的平均总云量，取值范围 `[0, 1]`

<!-- END_DOC_46 -->

---


<!-- START_DOC_47: api_console_stats.md (URL: https://dev.qweather.com/docs/api/console/stats) -->
Title: 请求量统计

URL Source: https://dev.qweather.com/docs/api/console/stats

Markdown Content:
[和风天气开发者服务](https://dev.qweather.com/)

[控制台](https://id.qweather.com/#/login?redirect=https://console.qweather.com)

*   [首页](https://dev.qweather.com/)
*   [文档](https://dev.qweather.com/docs/)
*   [图标](https://icons.qweather.com/)
*   [展示](https://dev.qweather.com/showcase/)
*   [帮助](https://dev.qweather.com/help/)
*   [价格](https://dev.qweather.com/price/)
*   Search K 

*   [文档](https://dev.qweather.com/docs/)
*   >
*   [开发文档](https://dev.qweather.com/docs/api/)
*   >
*   [控制台API](https://dev.qweather.com/docs/api/console/)
*   >
*   [请求量统计](https://dev.qweather.com/docs/api/console/stats/)

#### [开始使用](https://dev.qweather.com/docs/start/)

*   [特性](https://dev.qweather.com/docs/features/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [帐号管理](https://dev.qweather.com/docs/account/)

#### [开发文档](https://dev.qweather.com/docs/api/)

*   [GeoAPI](https://dev.qweather.com/docs/api/geoapi/)
*   [天气预报](https://dev.qweather.com/docs/api/weather/)
*   [分钟预报](https://dev.qweather.com/docs/api/minutely/)
*   [预警](https://dev.qweather.com/docs/api/warning/)
*   [天气指数](https://dev.qweather.com/docs/api/indices/)
*   [空气质量](https://dev.qweather.com/docs/api/air-quality/)
*   [时光机](https://dev.qweather.com/docs/api/time-machine/)
*   [热带气旋（台风）](https://dev.qweather.com/docs/api/tropical-cyclone/)
*   [海洋数据](https://dev.qweather.com/docs/api/ocean/)
*   [太阳辐射](https://dev.qweather.com/docs/api/solar-radiation/)
*   [天文](https://dev.qweather.com/docs/api/astronomy/)
*   [控制台API](https://dev.qweather.com/docs/api/console/)

    *   [GET 财务汇总](https://dev.qweather.com/docs/api/console/finance/)
    *   [GET 请求量统计](https://dev.qweather.com/docs/api/console/stats/)

#### [实用资料](https://dev.qweather.com/docs/resource/)

*   [专用词汇表](https://dev.qweather.com/docs/resource/glossary/)
*   [错误码](https://dev.qweather.com/docs/resource/error-code/)
*   [多语言](https://dev.qweather.com/docs/resource/language/)
*   [常见城市列表](https://dev.qweather.com/docs/resource/location-list/)
*   [元数据](https://dev.qweather.com/docs/resource/metadata/)
*   [单位](https://dev.qweather.com/docs/resource/unit/)

#### [最佳实践](https://dev.qweather.com/docs/best-practices/)

*   [不要假设](https://dev.qweather.com/docs/best-practices/no-assumptions/)
*   [优化请求](https://dev.qweather.com/docs/best-practices/optimize-requests/)
*   [处理Gzip](https://dev.qweather.com/docs/best-practices/gzip/)
*   [缓存你的数据](https://dev.qweather.com/docs/best-practices/cache/)
*   [安全指南](https://dev.qweather.com/docs/best-practices/security-guidelines/)

#### [财务与费用](https://dev.qweather.com/docs/finance/)

*   [计费方式和支付](https://dev.qweather.com/docs/finance/billing-and-payment/)
*   [按量计费定价](https://dev.qweather.com/docs/finance/pricing/)
*   [节省计划](https://dev.qweather.com/docs/finance/savings-plans/)
*   [增值税发票](https://dev.qweather.com/docs/finance/vat-invoice/)

#### [条款](https://dev.qweather.com/docs/terms/)

*   [使用限制](https://dev.qweather.com/docs/terms/restriction/)
*   [注明来源](https://dev.qweather.com/docs/terms/attribution/)
*   [服务条款](https://dev.qweather.com/docs/terms/tos/)

# 请求量统计

查询最近24小时的API请求量统计。

> **提示：** 返回的数据截止至上一个小时或更早时候，它与控制台中显示的数据相比可能有1个小时或更长的延迟。

## 获取权限[#](https://dev.qweather.com/docs/api/console/stats#privileges)

访问本接口必须由帐号所有者在控制台为凭据开通权限。

1.   [前往控制台-项目管理](https://console.qweather.com/project)
2.   点击需要启用控制台API的凭据
3.   向下滑动至“控制台权限”
4.   在“指标和统计”中勾选所需要的权限： 
    *   “允许访问请求量汇总统计”，汇总所有项目和凭据的请求量，包括已删除的
    *   “允许访问指定项目的请求量统计”，使用`project`参数过滤
    *   “允许访问指定凭据的请求量统计”，使用`credential`参数过滤

5.   点击“保存”按钮

请注意：已删除的项目或凭据无法单独查询（例如使用`project`或`credential`参数），但其请求量仍计入汇总统计中。

## 请求路径[#](https://dev.qweather.com/docs/api/console/stats#endpoint)

`GET /metrics/v1/stats`

## 参数[#](https://dev.qweather.com/docs/api/console/stats#parameters)

#### 查询参数

*   project string  指定项目ID以查看该项目的请求量统计。`project`与`credential`互斥。  
*   credential string  指定凭据ID以查看该凭据的请求量统计。`credential`与`project`互斥。  

## 请求示例[#](https://dev.qweather.com/docs/api/console/stats#request-example)

```bash
curl -X GET --compressed \
-H 'Authorization: Bearer your_token' \
'https://your-api-host/metrics/v1/stats'
```

请将`your_token`替换为你的[JWT身份认证](https://dev.qweather.com/docs/configuration/authentication/)，将`your_api_host`替换为你的[API Host](https://dev.qweather.com/docs/configuration/api-host/)

[试一下](https://dev.qweather.com/api-explore/#/Console/getConsoleStats)

## 返回数据[#](https://dev.qweather.com/docs/api/console/stats#response)

```json
{
  "metadata": {
    "tag": "dcd6183920ebf9f5aa3fd066ed68e61b"
  },
  "asOf": "2025-05-12T02:59Z",
  "success": [
    {
      "api": "Weather",
      "hours": [
        482,
        475,
        520,
        465,
        481,
        485,
        477,
        471,
        518,
        448,
        462,
        461,
        441,
        441,
        512,
        441,
        441,
        441,
        486,
        511,
        582,
        543,
        551,
        525
      ]
    },
    {
      "api": "Geo",
      "hours": [
        20,
        16,
        0,
        15,
        17,
        18,
        18,
        15,
        0,
        4,
        2,
        20,
        0,
        0,
        0,
        0,
        0,
        0,
        17,
        40,
        29,
        21,
        26,
        39
      ]
    },
    {
      "api": "WeatherAlert",
      "hours": [
        436,
        643,
        460,
        507,
        527,
        957,
        716,
        700,
        687,
        1014,
        706,
        557,
        559,
        799,
        280,
        464,
        452,
        764,
        472,
        488,
        496,
        806,
        479,
        556
      ]
    },
    {
      "api": "WeatherIndices",
      "hours": [
        16,
        15,
        0,
        9,
        17,
        16,
        14,
        13,
        0,
        0,
        0,
        10,
        0,
        0,
        0,
        0,
        0,
        0,
        17,
        34,
        29,
        19,
        26,
        29
      ]
    },
    {
      "api": "AirQuality",
      "hours": [
        16,
        15,
        0,
        9,
        17,
        16,
        14,
        13,
        0,
        0,
        0,
        10,
        0,
        0,
        0,
        0,
        0,
        0,
        17,
        34,
        29,
        19,
        26,
        29
      ]
    }
  ],
  "errors": [
    {
      "api": "Weather",
      "hours": [
        3,
        1,
        4,
        3,
        3,
        3,
        3,
        3,
        4,
        0,
        1,
        3,
        3,
        3,
        4,
        3,
        3,
        3,
        3,
        3,
        4,
        3,
        3,
        3
      ]
    },
    {
      "api": "WeatherAlert",
      "hours": [
        0,
        0,
        0,
        0,
        0,
        1,
        1,
        1,
        1,
        0,
        1,
        0,
        0,
        0,
        0,
        4,
        2,
        1,
        1,
        1,
        1,
        1,
        1,
        1
      ]
    }
  ]
}
```

*   metadata object  [元数据](https://dev.qweather.com/docs/resource/metadata/)  
    *   tag string  数据唯一标识  
    *   attributions array  数据归因信息或声明，必须与当前数据共同显示  

*   asOf date-time  当前数据的截止日期  
*   success array  成功的请求数据  
    *   api string  成功请求的API名称  
    *   hours array  最近24小时每小时的成功请求量，结束时间以`asOf`为准。例如 asOf=2025-03-20T09:59Z，则数组中的最后一条数据代表09:00 ～ 09:59（UTC）的请求量  

*   errors array  错误的请求数据  
    *   api string  错误请求的API名称  
    *   hours array  最近24小时每小时的错误请求量，结束时间以`asOf`为准。例如 asOf=2025-03-20T09:59Z 则数组中的最后一条数据代表09:00 ～ 09:59（UTC）的请求量  

#### 本页导航

*   [请求路径](https://dev.qweather.com/docs/api/console/stats#endpoint)
*   [参数](https://dev.qweather.com/docs/api/console/stats#parameters)
*   [请求示例](https://dev.qweather.com/docs/api/console/stats#request-example)
*   [返回数据](https://dev.qweather.com/docs/api/console/stats#response)

### [和风天气开发者服务](https://dev.qweather.com/)

[联系](https://www.qweather.com/contact/)• [博客](https://blog.qweather.com/)• [GitHub](https://github.com/qwd)

Language 

### 文档

*   [开始使用](https://dev.qweather.com/docs/start/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [开发文档](https://dev.qweather.com/docs/api/)
*   [财务与费用](https://dev.qweather.com/docs/finance/)
*   [最佳实践](https://dev.qweather.com/docs/best-practices/)
*   [实用资料](https://dev.qweather.com/docs/resource/)

### 特性

*   [服务和数据](https://dev.qweather.com/docs/features/service-and-data/)
*   [全球部署](https://dev.qweather.com/docs/features/global-deployment/)
*   [性能](https://dev.qweather.com/docs/features/performance/)

### 更多

*   [天气预报](https://www.qweather.com/)
*   [天气地图](https://map.qweather.com/)
*   [下载APP](https://www.qweather.com/app/)

*   © 2026 [QWeather](https://www.qweather.com/en/)
*   [许可协议](https://www.qweather.com/terms/developers-eula/)
*   [隐私政策](https://www.qweather.com/terms/privacy)
*   [免责声明](https://www.qweather.com/terms/disclaimer/)

[京ICP备15048401号-11](https://beian.miit.gov.cn/)

<!-- END_DOC_47 -->

---


<!-- START_DOC_48: features_global-deployment.md (URL: https://dev.qweather.com/docs/features/global-deployment) -->
Title: 全球部署

URL Source: https://dev.qweather.com/docs/features/global-deployment

Markdown Content:
## 全球部署

和风天气开发服务提供了全球部署的能力，你不再需要面对各国不同的文化和本地化问题或者世界各地复杂的网络环境。

使用全球部署能力不需要你进行额外的配置，这一切几乎都是自动化的。

## 多语言[#](https://dev.qweather.com/docs/features/global-deployment#multi-language)

和风天气开发服务支持30+语言和本地语言，你只需要简单的在请求中添加多语言参数即可。

查看[我们支持的语言](https://dev.qweather.com/docs/resource/language/)。

## 全球城市覆盖[#](https://dev.qweather.com/docs/features/global-deployment#global-city-coverage)

目前我们已经为全球超过50万个城市或地区提供气象服务，使用我们的地理信息服务，可以轻松的获取这些城市的信息。

查看[地理信息服务](https://dev.qweather.com/docs/api/geoapi/)。

## 全球加速[#](https://dev.qweather.com/docs/features/global-deployment#global-acceleration)

![Image 1: global-server](https://dev.qweather.com/assets/images/content/global-server-flow.png)

在你访问API服务的背后，是遍布全球10个和风天气的数据中心和上百个CDN节点，这让你的应用在世界各地都可以快速稳定的获取天气服务。

*   **自动路由：** 无论你的应用部署在哪里，你的数据请求都会自动路由至最近的数据中心。
*   **负载均衡：** 当某一个数据中心发生故障或负载较高时，你的请求将被自动分配到正常运行的数据中心，确保你的应用永远在线。
*   **CDN边缘加速：** 在未建有数据中心的地区，我们将提供CDN边缘加速能力，稳定的传输你的请求。
*   **自定义数据中心：** 对于企业开发者，我们提供在指定国家或地区建立独享的数据中心，这将极大的加速你在当地的请求。

<!-- END_DOC_48 -->

---


<!-- START_DOC_49: api_tropical-cyclone.md (URL: https://dev.qweather.com/docs/api/tropical-cyclone) -->
Title: 热带气旋（台风）

URL Source: https://dev.qweather.com/docs/api/tropical-cyclone

Markdown Content:
[和风天气开发者服务](https://dev.qweather.com/)

[控制台](https://id.qweather.com/#/login?redirect=https://console.qweather.com)

*   [首页](https://dev.qweather.com/)
*   [文档](https://dev.qweather.com/docs/)
*   [图标](https://icons.qweather.com/)
*   [展示](https://dev.qweather.com/showcase/)
*   [帮助](https://dev.qweather.com/help/)
*   [价格](https://dev.qweather.com/price/)
*   Search K 

*   [文档](https://dev.qweather.com/docs/)
*   >
*   [开发文档](https://dev.qweather.com/docs/api/)
*   >
*   [热带气旋（台风）](https://dev.qweather.com/docs/api/tropical-cyclone/)

#### [开始使用](https://dev.qweather.com/docs/start/)

*   [特性](https://dev.qweather.com/docs/features/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [帐号管理](https://dev.qweather.com/docs/account/)

#### [开发文档](https://dev.qweather.com/docs/api/)

*   [GeoAPI](https://dev.qweather.com/docs/api/geoapi/)
*   [天气预报](https://dev.qweather.com/docs/api/weather/)
*   [分钟预报](https://dev.qweather.com/docs/api/minutely/)
*   [预警](https://dev.qweather.com/docs/api/warning/)
*   [天气指数](https://dev.qweather.com/docs/api/indices/)
*   [空气质量](https://dev.qweather.com/docs/api/air-quality/)
*   [时光机](https://dev.qweather.com/docs/api/time-machine/)
*   [热带气旋（台风）](https://dev.qweather.com/docs/api/tropical-cyclone/)

    *   [GET 台风预报](https://dev.qweather.com/docs/api/tropical-cyclone/storm-forecast/)
    *   [GET 台风实况和路径](https://dev.qweather.com/docs/api/tropical-cyclone/storm-track/)
    *   [GET 台风列表](https://dev.qweather.com/docs/api/tropical-cyclone/storm-list/)

*   [海洋数据](https://dev.qweather.com/docs/api/ocean/)
*   [太阳辐射](https://dev.qweather.com/docs/api/solar-radiation/)
*   [天文](https://dev.qweather.com/docs/api/astronomy/)
*   [控制台API](https://dev.qweather.com/docs/api/console/)

#### [实用资料](https://dev.qweather.com/docs/resource/)

*   [专用词汇表](https://dev.qweather.com/docs/resource/glossary/)
*   [错误码](https://dev.qweather.com/docs/resource/error-code/)
*   [多语言](https://dev.qweather.com/docs/resource/language/)
*   [常见城市列表](https://dev.qweather.com/docs/resource/location-list/)
*   [元数据](https://dev.qweather.com/docs/resource/metadata/)
*   [单位](https://dev.qweather.com/docs/resource/unit/)

#### [最佳实践](https://dev.qweather.com/docs/best-practices/)

*   [不要假设](https://dev.qweather.com/docs/best-practices/no-assumptions/)
*   [优化请求](https://dev.qweather.com/docs/best-practices/optimize-requests/)
*   [处理Gzip](https://dev.qweather.com/docs/best-practices/gzip/)
*   [缓存你的数据](https://dev.qweather.com/docs/best-practices/cache/)
*   [安全指南](https://dev.qweather.com/docs/best-practices/security-guidelines/)

#### [财务与费用](https://dev.qweather.com/docs/finance/)

*   [计费方式和支付](https://dev.qweather.com/docs/finance/billing-and-payment/)
*   [按量计费定价](https://dev.qweather.com/docs/finance/pricing/)
*   [节省计划](https://dev.qweather.com/docs/finance/savings-plans/)
*   [增值税发票](https://dev.qweather.com/docs/finance/vat-invoice/)

#### [条款](https://dev.qweather.com/docs/terms/)

*   [使用限制](https://dev.qweather.com/docs/terms/restriction/)
*   [注明来源](https://dev.qweather.com/docs/terms/attribution/)
*   [服务条款](https://dev.qweather.com/docs/terms/tos/)

# 热带气旋（台风）

热带气旋 API 提供中国地区的台风信息，包括台风实时位置、路径和预报数据。

API

[GET 台风预报](https://dev.qweather.com/docs/api/tropical-cyclone/storm-forecast/)
获取全球主要海洋流域中指定台风的预测位置、等级、气压和风速等数据。

[GET 台风实况和路径](https://dev.qweather.com/docs/api/tropical-cyclone/storm-track/)
获取全球主要海洋流域中指定台风的实时位置、强度及路径数据。

[GET 台风列表](https://dev.qweather.com/docs/api/tropical-cyclone/storm-list/)
获取全球主要海洋流域最近2年的台风列表。

### [和风天气开发者服务](https://dev.qweather.com/)

[联系](https://www.qweather.com/contact/)• [博客](https://blog.qweather.com/)• [GitHub](https://github.com/qwd)

Language 

### 文档

*   [开始使用](https://dev.qweather.com/docs/start/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [开发文档](https://dev.qweather.com/docs/api/)
*   [财务与费用](https://dev.qweather.com/docs/finance/)
*   [最佳实践](https://dev.qweather.com/docs/best-practices/)
*   [实用资料](https://dev.qweather.com/docs/resource/)

### 特性

*   [服务和数据](https://dev.qweather.com/docs/features/service-and-data/)
*   [全球部署](https://dev.qweather.com/docs/features/global-deployment/)
*   [性能](https://dev.qweather.com/docs/features/performance/)

### 更多

*   [天气预报](https://www.qweather.com/)
*   [天气地图](https://map.qweather.com/)
*   [下载APP](https://www.qweather.com/app/)

*   © 2026 [QWeather](https://www.qweather.com/en/)
*   [许可协议](https://www.qweather.com/terms/developers-eula/)
*   [隐私政策](https://www.qweather.com/terms/privacy)
*   [免责声明](https://www.qweather.com/terms/disclaimer/)

[京ICP备15048401号-11](https://beian.miit.gov.cn/)

<!-- END_DOC_49 -->

---


<!-- START_DOC_50: api_geoapi_top-city.md (URL: https://dev.qweather.com/docs/api/geoapi/top-city) -->
Title: 热门城市查询

URL Source: https://dev.qweather.com/docs/api/geoapi/top-city

Markdown Content:
[和风天气开发者服务](https://dev.qweather.com/)

[控制台](https://id.qweather.com/#/login?redirect=https://console.qweather.com)

*   [首页](https://dev.qweather.com/)
*   [文档](https://dev.qweather.com/docs/)
*   [图标](https://icons.qweather.com/)
*   [展示](https://dev.qweather.com/showcase/)
*   [帮助](https://dev.qweather.com/help/)
*   [价格](https://dev.qweather.com/price/)
*    

*   [文档](https://dev.qweather.com/docs/)
*   >
*   [开发文档](https://dev.qweather.com/docs/api/)
*   >
*   [GeoAPI](https://dev.qweather.com/docs/api/geoapi/)
*   >
*   [热门城市查询](https://dev.qweather.com/docs/api/geoapi/top-city/)

#### [开始使用](https://dev.qweather.com/docs/start/)

*   [特性](https://dev.qweather.com/docs/features/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [帐号管理](https://dev.qweather.com/docs/account/)

#### [开发文档](https://dev.qweather.com/docs/api/)

*   [GeoAPI](https://dev.qweather.com/docs/api/geoapi/)

    *   [GET 城市搜索](https://dev.qweather.com/docs/api/geoapi/city-lookup/)
    *   [GET 热门城市查询](https://dev.qweather.com/docs/api/geoapi/top-city/)
    *   [GET POI搜索](https://dev.qweather.com/docs/api/geoapi/poi-lookup/)
    *   [GET POI范围搜索](https://dev.qweather.com/docs/api/geoapi/poi-range/)

*   [天气预报](https://dev.qweather.com/docs/api/weather/)
*   [分钟预报](https://dev.qweather.com/docs/api/minutely/)
*   [预警](https://dev.qweather.com/docs/api/warning/)
*   [天气指数](https://dev.qweather.com/docs/api/indices/)
*   [空气质量](https://dev.qweather.com/docs/api/air-quality/)
*   [时光机](https://dev.qweather.com/docs/api/time-machine/)
*   [热带气旋（台风）](https://dev.qweather.com/docs/api/tropical-cyclone/)
*   [海洋数据](https://dev.qweather.com/docs/api/ocean/)
*   [太阳辐射](https://dev.qweather.com/docs/api/solar-radiation/)
*   [天文](https://dev.qweather.com/docs/api/astronomy/)
*   [控制台API](https://dev.qweather.com/docs/api/console/)

#### [实用资料](https://dev.qweather.com/docs/resource/)

*   [专用词汇表](https://dev.qweather.com/docs/resource/glossary/)
*   [错误码](https://dev.qweather.com/docs/resource/error-code/)
*   [多语言](https://dev.qweather.com/docs/resource/language/)
*   [常见城市列表](https://dev.qweather.com/docs/resource/location-list/)
*   [元数据](https://dev.qweather.com/docs/resource/metadata/)
*   [单位](https://dev.qweather.com/docs/resource/unit/)

#### [最佳实践](https://dev.qweather.com/docs/best-practices/)

*   [不要假设](https://dev.qweather.com/docs/best-practices/no-assumptions/)
*   [优化请求](https://dev.qweather.com/docs/best-practices/optimize-requests/)
*   [处理Gzip](https://dev.qweather.com/docs/best-practices/gzip/)
*   [缓存你的数据](https://dev.qweather.com/docs/best-practices/cache/)
*   [安全指南](https://dev.qweather.com/docs/best-practices/security-guidelines/)

#### [财务与费用](https://dev.qweather.com/docs/finance/)

*   [计费方式和支付](https://dev.qweather.com/docs/finance/billing-and-payment/)
*   [按量计费定价](https://dev.qweather.com/docs/finance/pricing/)
*   [节省计划](https://dev.qweather.com/docs/finance/savings-plans/)
*   [增值税发票](https://dev.qweather.com/docs/finance/vat-invoice/)

#### [条款](https://dev.qweather.com/docs/terms/)

*   [使用限制](https://dev.qweather.com/docs/terms/restriction/)
*   [注明来源](https://dev.qweather.com/docs/terms/attribution/)
*   [服务条款](https://dev.qweather.com/docs/terms/tos/)

# 热门城市查询

获取全球各国热门城市列表。 
## 请求路径[#](https://dev.qweather.com/docs/api/geoapi/top-city#endpoint)

`GET /geo/v2/city/top`

## 参数[#](https://dev.qweather.com/docs/api/geoapi/top-city#parameters)

#### 查询参数

*   range string  搜索范围，可设定只在某个国家或地区范围内进行搜索，国家和地区名称需使用[ISO 3166 所定义的国家代码](https://dev.qweather.com/docs/resource/glossary/#iso-3166)。如果不设置此参数，搜索范围将在所有城市。例如 `range=cn`  
*   number integer  返回结果的数量，取值范围1-20，默认返回10个结果。  
*   lang string  [多语言设置](https://dev.qweather.com/docs/resource/language/)  

## 请求示例[#](https://dev.qweather.com/docs/api/geoapi/top-city#request-example)

```bash
curl -X GET --compressed \
-H 'Authorization: Bearer your_token' \
'https://your-api-host/geo/v2/city/top'
```

请将`your_token`替换为你的[JWT身份认证](https://dev.qweather.com/docs/configuration/authentication/)，将`your_api_host`替换为你的[API Host](https://dev.qweather.com/docs/configuration/api-host/)

[试一下](https://dev.qweather.com/api-explore/#/Geo/getGeoTopcity)

## 返回数据[#](https://dev.qweather.com/docs/api/geoapi/top-city#response)

```json
{
  "code": "200",
  "topCityList": [
    {
      "name": "北京",
      "id": "101010100",
      "lat": "39.90499",
      "lon": "116.40529",
      "adm2": "北京",
      "adm1": "北京市",
      "country": "中国",
      "tz": "Asia/Shanghai",
      "utcOffset": "+08:00",
      "isDst": "0",
      "type": "city",
      "rank": "10",
      "fxLink": "https://www.qweather.com/weather/beijing-101010100.html"
    },
    {
      "name": "朝阳",
      "id": "101010300",
      "lat": "39.92149",
      "lon": "116.48641",
      "adm2": "北京",
      "adm1": "北京市",
      "country": "中国",
      "tz": "Asia/Shanghai",
      "utcOffset": "+08:00",
      "isDst": "0",
      "type": "city",
      "rank": "15",
      "fxLink": "https://www.qweather.com/weather/chaoyang-101010300.html"
    },
    {
      "name": "海淀",
      "id": "101010200",
      "lat": "39.95607",
      "lon": "116.31032",
      "adm2": "北京",
      "adm1": "北京市",
      "country": "中国",
      "tz": "Asia/Shanghai",
      "utcOffset": "+08:00",
      "isDst": "0",
      "type": "city",
      "rank": "15",
      "fxLink": "https://www.qweather.com/weather/haidian-101010200.html"
    },
    {
      "name": "深圳",
      "id": "101280601",
      "lat": "22.54700",
      "lon": "114.08595",
      "adm2": "深圳",
      "adm1": "广东省",
      "country": "中国",
      "tz": "Asia/Shanghai",
      "utcOffset": "+08:00",
      "isDst": "0",
      "type": "city",
      "rank": "13",
      "fxLink": "https://www.qweather.com/weather/shenzhen-101280601.html"
    },
    {
      "name": "上海",
      "id": "101020100",
      "lat": "31.23171",
      "lon": "121.47264",
      "adm2": "上海",
      "adm1": "上海市",
      "country": "中国",
      "tz": "Asia/Shanghai",
      "utcOffset": "+08:00",
      "isDst": "0",
      "type": "city",
      "rank": "11",
      "fxLink": "https://www.qweather.com/weather/shanghai-101020100.html"
    },
    {
      "name": "浦东新区",
      "id": "101020600",
      "lat": "31.24594",
      "lon": "121.56770",
      "adm2": "上海",
      "adm1": "上海市",
      "country": "中国",
      "tz": "Asia/Shanghai",
      "utcOffset": "+08:00",
      "isDst": "0",
      "type": "city",
      "rank": "15",
      "fxLink": "https://www.qweather.com/weather/pudong-101020600.html"
    }
  ],
  "refer": {
    "sources": [
      "https://developer.qweather.com/attribution.html"
    ],
    "license": [
      "QWeather Developers License"
    ]
  }
}
```

*   code string  [状态码](https://dev.qweather.com/docs/resource/error-code/)  
*   topCityList object  热门城市/地区列表  
    *   name string  位置名称  
    *   id string  位置ID  
    *   lat string  纬度  
    *   lon string  经度  
    *   adm2 string  上级行政区划名称  
    *   adm1 string  一级行政区域名称  
    *   country string  国家名称  
    *   tz string  [时区](https://dev.qweather.com/docs/resource/glossary/#timezone)  
    *   utcOffset string  当前位置与[UTC时间偏移的小时数](https://dev.qweather.com/docs/resource/glossary/#utc-offset)  
    *   isDst string  是否处于[夏令时](https://dev.qweather.com/docs/resource/glossary/#daylight-saving-time)。`1` 表示当前处于夏令时，`0` 表示当前不是夏令时  
    *   type string  位置的属性  
    *   rank string  [位置的评分](https://dev.qweather.com/docs/resource/glossary/#rank)  
    *   fxLink uri  该位置的天气预报网页链接，便于嵌入你的网站或应用  

*   refer object  数据来源和许可信息  
    *   sources array  原始数据来源，或数据源说明，可能为空  

    *   license array  数据许可或版权声明，可能为空  

#### 本页导航

*   [请求路径](https://dev.qweather.com/docs/api/geoapi/top-city#endpoint)
*   [参数](https://dev.qweather.com/docs/api/geoapi/top-city#parameters)
*   [请求示例](https://dev.qweather.com/docs/api/geoapi/top-city#request-example)
*   [返回数据](https://dev.qweather.com/docs/api/geoapi/top-city#response)

### [和风天气开发者服务](https://dev.qweather.com/)

[联系](https://www.qweather.com/contact/)• [博客](https://blog.qweather.com/)• [GitHub](https://github.com/qwd)

Language 

### 文档

*   [开始使用](https://dev.qweather.com/docs/start/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [开发文档](https://dev.qweather.com/docs/api/)
*   [财务与费用](https://dev.qweather.com/docs/finance/)
*   [最佳实践](https://dev.qweather.com/docs/best-practices/)
*   [实用资料](https://dev.qweather.com/docs/resource/)

### 特性

*   [服务和数据](https://dev.qweather.com/docs/features/service-and-data/)
*   [全球部署](https://dev.qweather.com/docs/features/global-deployment/)
*   [性能](https://dev.qweather.com/docs/features/performance/)

### 更多

*   [天气预报](https://www.qweather.com/)
*   [天气地图](https://map.qweather.com/)
*   [下载APP](https://www.qweather.com/app/)

*   © 2026 [QWeather](https://www.qweather.com/en/)
*   [许可协议](https://www.qweather.com/terms/developers-eula/)
*   [隐私政策](https://www.qweather.com/terms/privacy)
*   [免责声明](https://www.qweather.com/terms/disclaimer/)

[京ICP备15048401号-11](https://beian.miit.gov.cn/)

<!-- END_DOC_50 -->

---


<!-- START_DOC_51: api_astronomy_sunrise-sunset.md (URL: https://dev.qweather.com/docs/api/astronomy/sunrise-sunset) -->
Title: 日出日落

URL Source: https://dev.qweather.com/docs/api/astronomy/sunrise-sunset

Markdown Content:
*   [文档](https://dev.qweather.com/docs/)
*   [开发文档](https://dev.qweather.com/docs/api/)
*   [天文](https://dev.qweather.com/docs/api/astronomy/)
*   [日出日落](https://dev.qweather.com/docs/api/astronomy/sunrise-sunset/)

## 日出日落

获取全球任意地点未来60天的日出日落时间。 
## 请求路径[#](https://dev.qweather.com/docs/api/astronomy/sunrise-sunset#endpoint)

`GET /v7/astronomy/sun`

## 参数[#](https://dev.qweather.com/docs/api/astronomy/sunrise-sunset#parameters)

#### 查询参数

*   location 必选

string 
需要查询地区的[LocationID](https://dev.qweather.com/docs/resource/glossary/#locationid)或以英文逗号分隔的[经度,纬度坐标](https://dev.qweather.com/docs/resource/glossary/#coordinate)（十进制，最多支持小数点后两位），LocationID可通过[GeoAPI](https://dev.qweather.com/docs/api/geoapi/)获取。例如 `location=101010100` 或 `location=116.41,39.92` 
*   date 必选

date 
选择日期，最多可选择未来60天（包含今天）的数据。日期格式为yyyyMMdd，例如 `date=20200531` 

## 请求示例[#](https://dev.qweather.com/docs/api/astronomy/sunrise-sunset#request-example)

```
curl -X GET --compressed \
-H 'Authorization: Bearer your_token' \
'https://your-api-host/v7/astronomy/sun?location=116.41%2C39.92&date=20200531'
```

请将`your_token`替换为你的[JWT身份认证](https://dev.qweather.com/docs/configuration/authentication/)，将`your_api_host`替换为你的[API Host](https://dev.qweather.com/docs/configuration/api-host/)

[试一下](https://dev.qweather.com/api-explore/#/Astronomy/getAstronomSun)

## 返回数据[#](https://dev.qweather.com/docs/api/astronomy/sunrise-sunset#response)

*   code

string 
*   updateTime

date-time 
*   fxLink

uri 
当前数据的响应式页面，便于嵌入网站或应用 
*   sunrise

string 
*   sunset

string 
*   refer

object 
数据来源和许可信息 
    *   sources

array 
原始数据来源，或数据源说明，可能为空 

    *   license

array 
数据许可或版权声明，可能为空

<!-- END_DOC_51 -->

---


<!-- START_DOC_52: configuration_authentication.md (URL: https://dev.qweather.com/docs/configuration/authentication) -->
Title: 身份认证

URL Source: https://dev.qweather.com/docs/configuration/authentication

Markdown Content:
## 身份认证

和风天气开发服务使用 JWT(JSON Web Token) 以及 API KEY 的方式进行身份认证。我们推荐使用JWT作为首选的身份认证方式，这将极大的提高安全性。

## JSON Web Token[#](https://dev.qweather.com/docs/configuration/authentication#json-web-token)

JSON Web Token（JWT）是一种开放标准（[RFC 7519](https://www.rfc-editor.org/rfc/rfc7519)），用于在各方安全的传递信息。无论在前端还是后端，JWT身份认证都能够显著的提高API安全等级，有效防止其他人伪造你的身份进行API请求。

和风天气 JWT 使用Ed25519算法进行签名，Ed25519是使用Curve25519椭圆曲线和SHA-512的EdDSA（Edwards-curve Digital Signature Algorithm）的一种实现。你需要提前生成Ed25519的私钥和公钥，其中私钥用于签名且由你自己保管，公钥用于我们对签名进行验证。这意味着除了你之外，任何人（包括我们）都无法伪造你的签名。

### 生成Ed25519密钥[#](https://dev.qweather.com/docs/configuration/authentication#generate-ed25519-key)

你可以使用熟悉的开发语言或第三方库在本地生成Ed25519密钥，或参考下列方法。生成密钥后，你需要将公钥添加到和风天气控制台，用于JWT身份验证。

#### 使用终端

建议OpenSSL v3+，绝大多数Linux和macOS已默认支持；Windows需提前安装OpenSSL。

打开终端分别粘贴下列文本并回车:

```
openssl genpkey -algorithm ED25519 -out ed25519-private.pem
openssl pkey -pubout -in ed25519-private.pem > ed25519-public.pem
```

这将在当前目录创建两个文件：

*   ed25519-private.pem，私钥，用于JWT认证的签名。你应该妥善安全的保管私钥。
*   ed25519-public.pem，公钥，用于签名的验证，需要上传到和风天气控制台。

#### 使用浏览器

支持 Chrome 137+，Edge 137+，Firefox 129+，Safari 17+。

在浏览器中打开控制台（通常按F12）输入下列代码并回车:

```
async function generateEd25519Pem() {
  const k = await crypto.subtle.generateKey({name:"Ed25519"},true,["sign","verify"]);
  const p8 = await crypto.subtle.exportKey("pkcs8",k.privateKey);
  const spki = await crypto.subtle.exportKey("spki",k.publicKey);
  const pem = (d,t)=>{
    let b=btoa(String.fromCharCode(...new Uint8Array(d)));
    return`-----BEGIN ${t}-----\n${b.match(/.{1,64}/g).join("\n")}\n-----END ${t}-----`;
  };
  const priv=pem(p8,"PRIVATE KEY");
  const pub=pem(spki,"PUBLIC KEY");
  console.log("PrivateKey:\n",priv,"\n\nPublicKey:\n",pub);
  return{priv,pub};
}
generateEd25519Pem();
```

你将看到输出的密钥：

*   PrivateKey，私钥。
*   PublicKey，公钥。

#### 使用JWT工具

参考下方 [JWT调试工具](https://dev.qweather.com/docs/configuration/authentication/#jwt-debugging)

### 上传公钥[#](https://dev.qweather.com/docs/configuration/authentication#upload-public-key)

当你完成密钥对的生成后，你需要将其中的公钥添加到和风天气控制台，用于JWT身份验证。

1.   [前往控制台-项目管理](https://console.qweather.com/project)

2.   在项目列表中点击你需要添加凭据的项目

3.   点击凭据区域右侧的“添加凭据”按钮

4.   输入凭据名称

5.   选择身份认证方式JSON Web Token

6.   使用任意文本编辑器打开公钥文件（比如刚才创建的ed25519-public.pem），复制其中的全部内容，这些内容看起来像是：

```
-----BEGIN PUBLIC KEY-----
MCowBQYDK2VwAyEAARbeZ5AhklFG4gg1Gx5g5bWxMMdsUd6b2MC4wV0/M9Q=
-----END PUBLIC KEY-----
```
7.   在公钥文本框中粘贴公钥内容

8.   点击“保存”按钮

你将在最后看到创建凭据成功的页面，并且显示了这个凭据的创建日期、ID和SHA256值。出于安全考虑，控制台不会再次显示这个公钥。但你可以使用公钥的SHA256值与本地SHA256进行对比，以便确认使用的是正确的公钥（上传的公钥会自动删除首尾的空白符及换行符之后再计算SHA256）。

### 生成JWT[#](https://dev.qweather.com/docs/configuration/authentication#generate-jwt)

和风天气支持标准的[JWT协议和规范](https://datatracker.ietf.org/doc/html/rfc7519)，大部分情况下你不需要自己编写生成JWT的代码，几乎所有开发语言都有开源库用于JWT的生成，你可以在[JWT.io](https://jwt.io/libraries)查看这些库。

一个完整的JWT包括三个部分：**Header**，**Payload**和**Signature**。我们将介绍每个部分必须传递的参数：

Header包括下列参数并保存为JSON对象格式：

*   `alg` 签名算法，请设置为**EdDSA**
*   `kid` 凭据ID，你可以在[控制台-项目管理](https://console.qweather.com/project)中查看

例如：

```
{
    "alg": "EdDSA",
    "kid": "ABCDE12345"
}
```

#### Payload

Payload 包括下列参数并保存为JSON对象格式：

*   `iss` 签发者，这个值是你的开发者ID，开发者ID在[控制台-设置](https://console.qweather.com/setting?lang=zh)中查看，是一个Q开头的10位字母/数字
*   `sub` 签发主体，这个值是凭据的项目ID，项目ID在[控制台-项目管理](https://console.qweather.com/project)中查看
*   `iat` 签发时间，这个值表示JWT签发生效的时间，UNIX时间戳格式。为了防止时间误差，建议你将`iat`设置为当前时间之前的30秒，并确保你的服务器或设备的时间和日期是正确的。
*   `exp` 过期时间，这个值表示JWT在何时过期，UNIX时间戳格式。较长的过期时间可以减轻负载，但是较短的时间可以提高安全性。你应该根据使用场景设置过期时间，例如在服务端，可能适合较长的时间，在前端则适合较短的时间。目前允许的有效期最长为24小时（86400秒）。

例如：

```
{
    "iss": "Q12345ABCD",
    "sub": "ABCDE23456",
    "iat": 1703912400,
    "exp": 1703912940
}
```

> **警告：** 在Header和Payload中的信息是明文传输，所以仅添加上述指定的参数，不要添加任何其他敏感信息和无关参数。

#### Signature

将Header和Payload分别进行Base64URL编码并用英文句号拼接在一起，使用你的私钥对其进行Ed25519算法的签名，之后对签名结果同样进行Base64URL编码。

> **注意：** 必须使用 **Base64URL** 编码，而不是 Base64，两者有些许差别。

#### 拼接在一起

最后，请将Base64URL编码后的Header、Payload和Signature使用英文句号拼接在一起，组合为最终的Token，即 `header.payload.signature`，最终看起来像是：

```
eyJhbGciOiAiRWREU0EiLCJraWQiOiAiQUJDRDEyMzQifQ.eyJpc3MiOiJBQkNEMTIzNCIsImlhdCI6MTcwMzkxMjQwMCwiZXhwIjoxNzAzOTEyOTQwfQ.MEQCIFGLmpmAEwuhB74mR04JWg_odEau6KYHYLRXs8Bp_miIAiBMU5O13vnv9ieEBSK71v4UULMI4K5T9El6bCxBkW4BdA
```

#### 保留字段

以下是 Header 和 Payload 的保留字段，暂时不参与身份认证。部分 JWT 库可能默认添加这些字段，建议​​移除它们，避免未来启用这些保留字段时影响你的JWT身份认证​​。

*   `typ` 如果包含此字段，必须设置为**JWT**
*   `aud`
*   `nbf`

将上述创建的完整Token作为参数添加到`Authorization: Bearer`请求标头，例如：

```
curl --compressed \
-H 'Authorization: Bearer eyJhbGciOiAiRWREU0EiLCJraWQiOiAiQUJDRDEyMzQifQ.eyJpc3MiOiJBQkNEMTIzNCIsImlhdCI6MTcwMzkxMjQwMCwiZXhwIjoxNzAzOTEyOTQwfQ.MEQCIFGLmpmAEwuhB74mR04JWg_odEau6KYHYLRXs8Bp_miIAiBMU5O13vnv9ieEBSK71v4UULMI4K5T9El6bCxBkW4BdA' \
'https://abcxyz.qweatherapi.com/weather/v1/current/39.92/116.41'
```

### JWT调试[#](https://dev.qweather.com/docs/configuration/authentication#jwt-debugging)

我们提供了两种调试工具：

**JWT Debugger**

这是一个开源、离线的JWT调试工具，用于生成Ed25519密钥对和创建JWT：

> **提示：** 本工具不能代替代码实现，请在项目中自行编码或使用三方库生成 JWT。

*   访问 **[https://jwt.qweather.com](https://jwt.qweather.com/)**
*   复制或下载 Ed25519 密钥，点击“重新生成”或刷新浏览器可生成新的密钥
*   点击橙色文字，替换为你的 `kid`，`iss`，`sub`，`iat`，`exp`，并在私钥区域粘贴你的私钥内容，随即生成JWT

**JWT Validator**

如希望检查JWT是否有效，或者API请求返回[401错误](https://dev.qweather.com/docs/resource/error-code/#unauthorized)，请登录控制台使用JWT验证工具检查你的Token：

> **提示：** 出于安全原因，只能验证与自己帐号匹配的JWT。

1.   [前往控制台-JWT验证](https://console.qweather.com/support/jwt-validation)
2.   将你的完整Token粘贴至文本框
3.   点击“验证”按钮

### 生成JWT示例[#](https://dev.qweather.com/docs/configuration/authentication#jwt-demo)

请将代码中的`YOUR_KEY_ID`，`YOUR_DEVELOPER_ID`，`YOUR_PROJECT_ID`，`YOUR_PRIVATE_KEY`或`PATH_OF_YOUR_PRIVATE_KEY`替换为你的值。

> **提示：** 示例仅供参考和测试，我们不保证在任何环境下可以正常运行，请根据你的开发语言和环境进行适配。

#### Java 15+

```
// Private key
String privateKeyString = "YOUR PRIVATE KEY";
privateKeyString = privateKeyString.replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "").trim();
byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyString);
PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
KeyFactory keyFactory = KeyFactory.getInstance("EdDSA");
PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

// Header
String headerJson = "{\"alg\": \"EdDSA\", \"kid\": \"YOUR_KEY_ID\"}";

// Payload
long iat = ZonedDateTime.now(ZoneOffset.UTC).toEpochSecond() - 30;
long exp = iat + 900;
String payloadJson = "{\"iss\": \"YOUR_DEVELOPER_ID\", \"sub\": \"YOUR_PROJECT_ID\", \"iat\": " + iat + ", \"exp\": " + exp + "}";

// Base64url header+payload
String headerEncoded = Base64.getUrlEncoder().encodeToString(headerJson.getBytes(StandardCharsets.UTF_8));
String payloadEncoded = Base64.getUrlEncoder().encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));
String data = headerEncoded + "." + payloadEncoded;

// Sign
Signature signer = Signature.getInstance("EdDSA");
signer.initSign(privateKey);
signer.update(data.getBytes(StandardCharsets.UTF_8));
byte[] signature = signer.sign();

String signatureEncoded = Base64.getUrlEncoder().encodeToString(signature);

String jwt = data + "." + signatureEncoded;

// Print Token
System.out.println("Signature:\n" + signatureEncoded);
System.out.println("JWT:\n" + jwt);
```

#### Java 8+

需要依赖库 [ed25519-java](https://github.com/str4d/ed25519-java)

```
<dependency>
    <groupId>net.i2p.crypto</groupId>
    <artifactId>eddsa</artifactId>
    <version>0.3.0</version>
</dependency>
```

```
// Private key
String privateKeyString = "YOUR PRIVATE KEY";
privateKeyString = privateKeyString.trim().replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "").trim();
byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyString);
PKCS8EncodedKeySpec encoded = new PKCS8EncodedKeySpec(privateKeyBytes);
PrivateKey privateKey = new EdDSAPrivateKey(encoded);

// Header
String headerJson = "{\"alg\": \"EdDSA\", \"kid\": \"YOUR_KEY_ID\"}";

// Payload
long iat = ZonedDateTime.now(ZoneOffset.UTC).toEpochSecond() - 30;
long exp = iat + 900;
String payloadJson = "{\"iss\": \"YOUR_DEVELOPER_ID\", \"sub\": \"YOUR_PROJECT_ID\", \"iat\": " + iat + ", \"exp\": " + exp + "}";

// Base64url header+payload
String headerEncoded = Base64.getUrlEncoder().encodeToString(headerJson.getBytes(StandardCharsets.UTF_8));
String payloadEncoded = Base64.getUrlEncoder().encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));
String data = headerEncoded + "." + payloadEncoded;

EdDSAParameterSpec spec = EdDSANamedCurveTable.getByName(EdDSANamedCurveTable.ED_25519);

// Sign
final Signature s = new EdDSAEngine(MessageDigest.getInstance(spec.getHashAlgorithm()));
s.initSign(privateKey);
s.update(data.getBytes(StandardCharsets.UTF_8));
byte[] signature = s.sign();

String signatureString = Base64.getUrlEncoder().encodeToString(signature);

System.out.println("Signature: \n" + signatureString);

// Print Token
String jwt = data + "." + signatureString;
System.out.println("JWT: \n" + jwt);
```

#### Node.js 16+

需安装Jose依赖，`npm install jose`

```
import {SignJWT, importPKCS8} from "jose";

const YourPrivateKey = 'YOUR_PRIVATE_KEY'

importPKCS8(YourPrivateKey, 'EdDSA').then((privateKey) => {
  const customHeader = {
    alg: 'EdDSA',
    kid: 'YOUR_KEY_ID'
  }
  const iat = Math.floor(Date.now() / 1000) - 30;
  const exp = iat + 900;
  const customPayload = {
    iss: 'YOUR_DEVELOPER_ID',
    sub: 'YOUR_PROJECT_ID',
    iat: iat,
    exp: exp
  }
  new SignJWT(customPayload)
    .setProtectedHeader(customHeader)
    .sign(privateKey)
    .then(token => console.log('JWT: ' + token))
}).catch((error) => console.error(error))
```

#### Python3

需要安装相应的依赖，运行 `pip3 install cryptography PyJWT`

```
#!/usr/bin/env python3
import sys
import time
import jwt

# Open PEM
private_key = """YOUR_PRIVATE_KEY"""

payload = {
    'iss': 'YOUR_DEVELOPER_ID',
    'sub': 'YOUR_PROJECT_ID',
    'iat': int(time.time()) - 30,
    'exp': int(time.time()) + 900
}
headers = {
    'kid': 'YOUR_KEY_ID'
}

# Generate JWT
encoded_jwt = jwt.encode(payload, private_key, algorithm='EdDSA', headers = headers)

print(f"JWT:  {encoded_jwt}")
```

#### PHP8.4+

```
function generateJWT($privateKeyPath, $kid, $iss, $sub) {
    $privateKey = file_get_contents($privateKeyPath);

    $header = base64_encode(json_encode(['alg' => 'EdDSA', 'kid' => $kid]));
    $payload = base64_encode(json_encode([
        'iss' => $iss,
        'sub' => $sub,
        'iat' => time() - 30,
        'exp' => time() + 900
    ]));

    $header = str_replace(['+', '/', '='], ['-', '_', ''], $header);
    $payload = str_replace(['+', '/', '='], ['-', '_', ''], $payload);

    // 签名
    $data = $header . '.' . $payload;
    $signature = '';
    $key = openssl_pkey_get_private($privateKey);
    openssl_sign($data, $signature, $key, 0);
    $signature = str_replace(['+', '/', '='], ['-', '_', ''], base64_encode($signature));

    return $data . '.' . $signature;
}

$jwt = generateJWT('YOUR_PRIVATE_KEY_PATH', 'YOUR_KEY_ID', 'YOUR_DEVELOPER_ID', 'YOUR_PROJECT_ID');
echo $jwt;
```

#### Shell

```
#!/bin/bash

# Set `kid`, `iss`, `sub` and `private_key_path`
kid=YOUR_KEY_ID
iss=YOUR_DEVELOPER_ID
sub=YOUR_PROJECT_ID
private_key_path=PATH_OF_YOUR_PRIVATE_KEY

# Set `iat` and `exp`
# `iat` defaults to the current time -30 seconds
# `exp` defaults to `iat` +15 minutes
iat=$(( $(date +%s) - 30 ))
exp=$((iat + 900))

# base64url encoded header and payload
header_base64=$(printf '{"alg":"EdDSA","kid":"%s"}' "$kid" | openssl base64 -e | tr -d '=' | tr '/+' '_-' | tr -d '\n')
payload_base64=$(printf '{"iss":"%s","sub":"%s","iat":%d,"exp":%d}' "$iss" "$sub" "$iat" "$exp" | openssl base64 -e | tr -d '=' | tr '/+' '_-' | tr -d '\n')
header_payload="${header_base64}.${payload_base64}"

# Save $header_payload as a temporary file for Ed25519 signature
tmp_file=$(mktemp)
echo -n "$header_payload" > "$tmp_file"

# Sign with Ed25519
signature=$(openssl pkeyutl -sign -inkey "$private_key_path" -rawin -in "$tmp_file" | openssl base64 | tr -d '=' | tr '/+' '_-' | tr -d '\n')

# Delete temporary file
rm -f "$tmp_file"

# Generate JWT
jwt="${header_payload}.${signature}"

# Print Token
echo "$jwt"
```

## API KEY[#](https://dev.qweather.com/docs/configuration/authentication#api-key)

API KEY是一种常见、操作简单的身份认证方式。相比较JWT而言，API KEY在一些场景下安全性较低。

> **注意：** 为了提高安全性，SDK 5+将不再支持API KEY。从2027年1月1日起，我们将限制使用API KEY进行身份认证的每日请求数量。

### 生成API KEY[#](https://dev.qweather.com/docs/configuration/authentication#generate-api-key)

你可以登录控制台快速的生成API KEY：

1.   点击左侧菜单中的“项目管理”
2.   点击需要添加API KEY的项目名称
3.   在凭据设置区域点击绿色的创建凭据按钮
4.   身份认证方式选择API KEY
5.   输入凭据名称，比如“旅游APP测试”
6.   点击创建按钮

你可以随时在[控制台-项目管理](https://console.qweather.com/project)中查看生成的API KEY。

### 发送API KEY请求[#](https://dev.qweather.com/docs/configuration/authentication#api-key-authorize-request)

> **注意：** 请不要同时使用多种身份认证方式，可能会导致身份认证失败。

我们支持两种形式使用API KEY进行身份验证:

#### 请求标头

在你的请求Header中加入`X-QW-Api-Key: your-key`，例如：

```
curl -H "X-QW-Api-Key: ABCD1234EFGH" --compressed \
'https://abcxyz.qweatherapi.com/weather/v1/current/39.92/116.41'
```

#### 请求参数

在请求参数中加入`key=your-key`，例如：

```
curl --compressed \
'https://abcxyz.qweatherapi.com/weather/v1/current/39.92/116.41?key=ABCD1234EFGH'
```

## API KEY数字签名[#](https://dev.qweather.com/docs/configuration/authentication#api-key-signature)

API KEY的数字签名方式已经不再被支持。

## 兼容性[#](https://dev.qweather.com/docs/configuration/authentication#compatibility)

参考下方表格了解不同服务对身份认证方式的兼容性。

|  | JWT | API KEY | API KEY数字签名 |
| --- | --- | --- | --- |
| API | ✅ | ✅ | ❌1 |
| SDK 4+2 | ❌ | ❌ | ✅ |
| SDK 5+ | ✅ | ❌ | ❌ |

_1: 仅2024-11-01前创建的凭据且仅用于2024-11-01前发布的 API，并在2026-12-31后完全停止支持。_

_2: SDK 4.x 将在2026-12-31日停止服务_

<!-- END_DOC_52 -->

---


<!-- START_DOC_53: api_time-machine.md (URL: https://dev.qweather.com/docs/api/time-machine) -->
Title: 时光机

URL Source: https://dev.qweather.com/docs/api/time-machine

Published Time: Thu, 27 Aug 2026 02:08:36 GMT

Markdown Content:
[和风天气开发者服务](https://dev.qweather.com/)

[控制台](https://id.qweather.com/#/login?redirect=https://console.qweather.com)

*   [首页](https://dev.qweather.com/)
*   [文档](https://dev.qweather.com/docs/)
*   [图标](https://icons.qweather.com/)
*   [展示](https://dev.qweather.com/showcase/)
*   [帮助](https://dev.qweather.com/help/)
*   [价格](https://dev.qweather.com/price/)
*   Search K 

*   [文档](https://dev.qweather.com/docs/)
*   >
*   [开发文档](https://dev.qweather.com/docs/api/)
*   >
*   [时光机](https://dev.qweather.com/docs/api/time-machine/)

#### [开始使用](https://dev.qweather.com/docs/start/)

*   [特性](https://dev.qweather.com/docs/features/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [帐号管理](https://dev.qweather.com/docs/account/)

#### [开发文档](https://dev.qweather.com/docs/api/)

*   [GeoAPI](https://dev.qweather.com/docs/api/geoapi/)
*   [天气预报](https://dev.qweather.com/docs/api/weather/)
*   [分钟预报](https://dev.qweather.com/docs/api/minutely/)
*   [预警](https://dev.qweather.com/docs/api/warning/)
*   [天气指数](https://dev.qweather.com/docs/api/indices/)
*   [空气质量](https://dev.qweather.com/docs/api/air-quality/)
*   [时光机](https://dev.qweather.com/docs/api/time-machine/)

    *   [GET 天气时光机](https://dev.qweather.com/docs/api/time-machine/time-machine-weather/)

*   [热带气旋（台风）](https://dev.qweather.com/docs/api/tropical-cyclone/)
*   [海洋数据](https://dev.qweather.com/docs/api/ocean/)
*   [太阳辐射](https://dev.qweather.com/docs/api/solar-radiation/)
*   [天文](https://dev.qweather.com/docs/api/astronomy/)
*   [控制台API](https://dev.qweather.com/docs/api/console/)

#### [实用资料](https://dev.qweather.com/docs/resource/)

*   [专用词汇表](https://dev.qweather.com/docs/resource/glossary/)
*   [错误码](https://dev.qweather.com/docs/resource/error-code/)
*   [多语言](https://dev.qweather.com/docs/resource/language/)
*   [常见城市列表](https://dev.qweather.com/docs/resource/location-list/)
*   [元数据](https://dev.qweather.com/docs/resource/metadata/)
*   [单位](https://dev.qweather.com/docs/resource/unit/)

#### [最佳实践](https://dev.qweather.com/docs/best-practices/)

*   [不要假设](https://dev.qweather.com/docs/best-practices/no-assumptions/)
*   [优化请求](https://dev.qweather.com/docs/best-practices/optimize-requests/)
*   [处理Gzip](https://dev.qweather.com/docs/best-practices/gzip/)
*   [缓存你的数据](https://dev.qweather.com/docs/best-practices/cache/)
*   [安全指南](https://dev.qweather.com/docs/best-practices/security-guidelines/)

#### [财务与费用](https://dev.qweather.com/docs/finance/)

*   [计费方式和支付](https://dev.qweather.com/docs/finance/billing-and-payment/)
*   [按量计费定价](https://dev.qweather.com/docs/finance/pricing/)
*   [节省计划](https://dev.qweather.com/docs/finance/savings-plans/)
*   [增值税发票](https://dev.qweather.com/docs/finance/vat-invoice/)

#### [条款](https://dev.qweather.com/docs/terms/)

*   [使用限制](https://dev.qweather.com/docs/terms/restriction/)
*   [注明来源](https://dev.qweather.com/docs/terms/attribution/)
*   [服务条款](https://dev.qweather.com/docs/terms/tos/)

# 时光机

时光机可以获取最近10天的历史天气。

> 和风天气额外提供了2000年至今的历史再分析气象数据，通过数据文件的形式发送，如需要长时间的历史气象数据数据，请提供下列信息，发送邮件至sales@qweather.com，我们将有专人与你联系:
> 
> 
> *   企业名称
> *   联系方式
> *   所需要的城市或坐标
> *   所需要的时间范围

API

[GET 天气时光机](https://dev.qweather.com/docs/api/time-machine/time-machine-weather/)
获取最近10天的天气历史再分析数据。

### [和风天气开发者服务](https://dev.qweather.com/)

[联系](https://www.qweather.com/contact/)• [博客](https://blog.qweather.com/)• [GitHub](https://github.com/qwd)

Language 

### 文档

*   [开始使用](https://dev.qweather.com/docs/start/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [开发文档](https://dev.qweather.com/docs/api/)
*   [财务与费用](https://dev.qweather.com/docs/finance/)
*   [最佳实践](https://dev.qweather.com/docs/best-practices/)
*   [实用资料](https://dev.qweather.com/docs/resource/)

### 特性

*   [服务和数据](https://dev.qweather.com/docs/features/service-and-data/)
*   [全球部署](https://dev.qweather.com/docs/features/global-deployment/)
*   [性能](https://dev.qweather.com/docs/features/performance/)

### 更多

*   [天气预报](https://www.qweather.com/)
*   [天气地图](https://map.qweather.com/)
*   [下载APP](https://www.qweather.com/app/)

*   © 2026 [QWeather](https://www.qweather.com/en/)
*   [许可协议](https://www.qweather.com/terms/developers-eula/)
*   [隐私政策](https://www.qweather.com/terms/privacy)
*   [免责声明](https://www.qweather.com/terms/disclaimer/)

[京ICP备15048401号-11](https://beian.miit.gov.cn/)

<!-- END_DOC_53 -->

---


<!-- START_DOC_54: api_air-quality_air-current.md (URL: https://dev.qweather.com/docs/api/air-quality/air-current) -->
Title: 实时空气质量

URL Source: https://dev.qweather.com/docs/api/air-quality/air-current

Published Time: Thu, 27 Aug 2026 02:08:36 GMT

Markdown Content:
## 实时空气质量

实时空气质量API提供全球指定地点的实时空气质量数据，精度为1x1公里，数据包括：

*   基于各个国家或地区当地标准的AQI、AQI等级、颜色和首要污染物
*   和风天气通用AQI
*   污染物浓度值、分指数
*   健康建议

## 请求路径[#](https://dev.qweather.com/docs/api/air-quality/air-current#endpoint)

`GET /airquality/v1/current/{latitude}/{longitude}`

## 参数[#](https://dev.qweather.com/docs/api/air-quality/air-current#parameters)

#### 路径参数

*   latitude 必选

number 
所需位置的纬度。十进制，最多支持小数点后两位。例如 `39.92` 
*   longitude 必选

number 
所需位置的经度。十进制，最多支持小数点后两位。例如 `116.41` 

#### 查询参数

*   lang

string 

## 请求示例[#](https://dev.qweather.com/docs/api/air-quality/air-current#request-example)

```
curl -X GET --compressed \
-H 'Authorization: Bearer your_token' \
'https://your-api-host/airquality/v1/current/39.92/116.41'
```

请将`your_token`替换为你的[JWT身份认证](https://dev.qweather.com/docs/configuration/authentication/)，将`your_api_host`替换为你的[API Host](https://dev.qweather.com/docs/configuration/api-host/)

[试一下](https://dev.qweather.com/api-explore/#/Air%20Quality/getAirqualityCurrent)

## 返回数据[#](https://dev.qweather.com/docs/api/air-quality/air-current#response)

*   metadata

object 
    *   tag

string 
数据唯一标识 
    *   attributions

array 
数据归因信息或声明，必须与当前数据共同显示 

*   indexes

array 
    *   code

string 
空气质量指数的code 
    *   name

string 
空气质量指数的名字 
    *   aqi

number 
空气质量指数的数值，包括将非数字形式的指数值转换为数字后的结果，便于程序计算 
    *   aqiDisplay

string 
空气质量指数的标准显示值，可能包含非数字字符，向用户展示时建议使用该字段 
    *   level

string 
空气质量指数等级 
    *   category

string 
空气质量指数类别 
    *   color

object 
空气质量指数的 RGBA 颜色表示 
        *   red

number 
红色分量值，取值范围 [0, 255] 
        *   green

number 
绿色分量值，取值范围 [0, 255] 
        *   blue

number 
蓝色分量值，取值范围 [0, 255] 
        *   alpha

number 
透明度分量值，取值范围 [0, 1] 

    *   primaryPollutant

object 
首要[污染物](https://dev.qweather.com/docs/api/air-quality/pollutant-list/#primary-pollutant)表示导致当前空气污染的主要成分 
        *   code

string 
首要污染物的code 
        *   name

string 
首要污染物的名字 
        *   fullName

string 
首要污染物的全称 

    *   health

object 
        *   effect

string 
空气质量对健康的影响，可能为空 
        *   advice

object 
健康建议，可能为空 
            *   generalPopulation

string 
对一般人群的健康指导意见，可能为空 
            *   sensitivePopulation

string 
对敏感人群的健康指导意见，可能为空 

*   pollutants

array 
    *   code

string 
污染物code，可选值：`pm10`、`pm2p5`、`co`、`no`、`no2`、`so2`、`o3`、`nmhc` 
    *   name

string 
污染物的名字 
    *   fullName

string 
污染物的全称 
    *   concentration

object 
污染物的浓度值 
        *   value

number 
数值 
        *   unit

string 

    *   subIndexes

array 
各项污染物的空气质量分指数列表，通常最差的污染物分指数代表当前的空气质量指数，并用于确定首要污染物 
        *   code

string 
分指数对应的空气质量指数代码 
        *   aqi

number 
分指数的数值 
        *   aqiDisplay

string 
分指数数值的显示名称

<!-- END_DOC_54 -->

---


<!-- START_DOC_55: api_weather_weather-current.md (URL: https://dev.qweather.com/docs/api/weather/weather-current) -->
Title: 实时天气

URL Source: https://dev.qweather.com/docs/api/weather/weather-current

Markdown Content:
## 实时天气

获取指定经纬度位置的实时天气数据，1公里分辨率，覆盖全球任意地点，分钟级更新。

实时天气包括：天气现象、温度、体感温度、相对湿度、风向和风速、阵风风速、降水量、海平面气压、能见度、露点温度、云量、紫外线指数等。

## 请求路径[#](https://dev.qweather.com/docs/api/weather/weather-current#endpoint)

`GET /weather/v1/current/{latitude}/{longitude}`

## 参数[#](https://dev.qweather.com/docs/api/weather/weather-current#parameters)

#### 路径参数

*   latitude 必选

number 
所需位置的纬度。十进制，最多支持小数点后两位。例如 `39.92` 
*   longitude 必选

number 
所需位置的经度。十进制，最多支持小数点后两位。例如 `116.41` 

#### 查询参数

*   localTime

boolean 是否返回查询地点的本地时间

可选值： `false` UTC时间（默认），`true` 本地时间  
*   lang

string 

## 请求示例[#](https://dev.qweather.com/docs/api/weather/weather-current#request-example)

```
curl -X GET --compressed \
-H 'Authorization: Bearer your_token' \
'https://your-api-host/weather/v1/current/39.92/116.41'
```

请将`your_token`替换为你的[JWT身份认证](https://dev.qweather.com/docs/configuration/authentication/)，将`your_api_host`替换为你的[API Host](https://dev.qweather.com/docs/configuration/api-host/)

[试一下](https://dev.qweather.com/api-explore/#/Weather/getWeatherCurrent)

## 返回数据[#](https://dev.qweather.com/docs/api/weather/weather-current#response)

*   metadata

object 
    *   tag

string 
数据唯一标识 
    *   attributions

array 
数据归因信息或声明，必须与当前数据共同显示 

*   condition

object 
    *   text

string 
天气现象的本地化描述 
    *   code

string 
天气现象代码 

*   temperature

object 
温度数据 
    *   value

number 
数值 
    *   unit

string 

*   feelsLike

object 
体感温度 
    *   value

number 
数值 
    *   unit

string 

*   humidity

number 
相对湿度，取值范围 `[0, 1]` 
*   wind

object 
风的数据 
    *   direction

object 
        *   degree

number 
风向角度，取值范围 `[0, 359]` 
        *   compass

string 风向的方位代码

可选值: `n`, `nne`, `ne`, `ene`, `e`, `ese`, `se`, `sse`, `s`, `ssw`, `sw`, `wsw`, `w`, `wnw`, `nw`, `nnw`, `none`, `vrb`  

    *   speed

object 
风速 
        *   value

number 
数值 
        *   unit

string 

    *   scale

number 

*   windGust

object 
阵风风速 
    *   value

number 
数值 
    *   unit

string 

*   precipitation

object 
降水数据 
    *   amount

object 
累计一小时降水量 
        *   value

number 
数值 
        *   unit

string 

    *   intensity

object 
降水强度 
        *   value

number 
数值 
        *   unit

string 

    *   type

string 
降水类型代码： `rain`（雨）、`snow`（雪）、`ice`（冰粒或冻雨）、 `mixed`（混合降水）、`none`（无降水）、`unknown`（未知） 

*   pressure

object 
海平面气压 
    *   value

number 
数值 
    *   unit

string 

*   visibility

object 
能见度 
    *   value

number 
数值 
    *   unit

string 

*   dewPoint

object 
露点温度 
    *   value

number 
数值 
    *   unit

string 

*   cloudCover

number 
云量，取值范围 `[0, 1]` 
*   uvIndex

number 
紫外线指数，取值范围 `[0, 15]`

<!-- END_DOC_55 -->

---


<!-- START_DOC_56: api_warning_weather-alert.md (URL: https://dev.qweather.com/docs/api/warning/weather-alert) -->
Title: 实时天气预警

URL Source: https://dev.qweather.com/docs/api/warning/weather-alert

Published Time: Thu, 27 Aug 2026 02:08:36 GMT

Markdown Content:
## 实时天气预警

根据指定经纬度查询全球国家和地区正在生效的官方天气预警信息。 
## 请求路径[#](https://dev.qweather.com/docs/api/warning/weather-alert#endpoint)

`GET /weatheralert/v1/current/{latitude}/{longitude}`

## 参数[#](https://dev.qweather.com/docs/api/warning/weather-alert#parameters)

#### 路径参数

*   latitude 必选

number 
所需位置的纬度。十进制，最多支持小数点后两位。例如 `39.92` 
*   longitude 必选

number 
所需位置的经度。十进制，最多支持小数点后两位。例如 `116.41` 

#### 查询参数

*   localTime

boolean 是否返回查询地点的本地时间

可选值： `false` UTC时间（默认），`true` 本地时间  
*   lang

string 

## 请求示例[#](https://dev.qweather.com/docs/api/warning/weather-alert#request-example)

```
curl -X GET --compressed \
-H 'Authorization: Bearer your_token' \
'https://your-api-host/weatheralert/v1/current/39.92/116.41'
```

请将`your_token`替换为你的[JWT身份认证](https://dev.qweather.com/docs/configuration/authentication/)，将`your_api_host`替换为你的[API Host](https://dev.qweather.com/docs/configuration/api-host/)

[试一下](https://dev.qweather.com/api-explore/#/Weather%20Alert/getWeatherAlertCurrent)

## 返回数据[#](https://dev.qweather.com/docs/api/warning/weather-alert#response)

*   metadata

object 
    *   tag

string 
数据唯一标识 
    *   attributions

array 
数据归因信息或声明，必须与当前数据共同显示 

    *   zeroResult

boolean 
**true** 表示请求成功，但无数据返回，例如查询地点无预警 

*   alerts

array 
预警信息列表 
    *   id

string 
本条预警信息的唯一标识 
    *   senderName

string 
预警发布机构的名称，可能为空 
    *   issuedTime

date-time 
原始预警信息生成的时间，实际发布或接收时间会略有延迟 
    *   messageType

object 
        *   
code

string 

信息性质的代码。可选值：

            *   `alert` 初始且活跃的预警信息
            *   `update` 更新并取代了在 `messageType.supersedes` 中指定预警信息
            *   `cancel` 取消了在 `messageType.supersedes` 中指定预警信息。**cancel** 性质的预警信息的有效期为 1 小时

        *   supersedes

array 
当前预警取代了之前预警的列表 

    *   eventType

object 
        *   name

string 
事件名称 
        *   code

string 
事件代码 

    *   
urgency

string 

预警信息的紧迫程度，可选值:

        *   `immediate` 必须立刻采取行动
        *   `expected` 应尽快采取行动（通常在 1 小时内）
        *   `future` 应在近期采取行动
        *   `past` 事件已不再发生
        *   `unknown` 紧迫性未知

    *   
severity

string 

预警信息的严重程度，可选值:

        *   `unknown`：严重性未知
        *   `minor`：对生命或财产构成的威胁极小或没有已知威胁
        *   `moderate`：对生命或财产可能构成威胁
        *   `severe`：对生命或财产构成的重大威胁
        *   `extreme`：对生命或财产构成的严重威胁

    *   
certainty

string 

预警信息的确定性或可信度，可选值：

        *   `observed` 事件已经发生或正在发生
        *   `likely` 发生概率 ≥ 50%
        *   `possible` 有可能发生，概率 ≤ 50%
        *   `unlikely` 预计不会发生，概率接近 0
        *   `unknown` 确定性未知

    *   icon

string 
预警对应的图标代码 
    *   color

object 
预警信息 RGBA 颜色表示 
        *   code

string 
颜色代码，可选值: `white`、`gray`、`green`、`blue`、`yellow`、`amber`、`orange`、`red`、`purple`、`black` 
        *   red

number 
红色分量值，取值范围 [0, 255] 
        *   green

number 
绿色分量值，取值范围 [0, 255] 
        *   blue

number 
蓝色分量值，取值范围 [0, 255] 
        *   alpha

number 
透明度分量值，取值范围 [0, 1] 

    *   effectiveTime

date-time 
预警信息的生效时间 
    *   onsetTime

date-time 
预警事件预计开始的时间，可能为空 
    *   expireTime

date-time 
预警信息的失效时间 
    *   headline

string 
预警信息的简要描述或标题 
    *   description

string 
预警信息的详细描述 
    *   criteria

string 
当前预警信息的触发标准或条件，仅供参考，可能滞后于官方标准 
    *   responseTypes

array 
对当前预警的应对方式的类型代码，可能为空 

    *   instruction

string 
对当前预警的防御指南或行动指导，可能为空

<!-- END_DOC_56 -->

---


<!-- START_DOC_57: resource.md (URL: https://dev.qweather.com/docs/resource) -->
Title: 实用资料

URL Source: https://dev.qweather.com/docs/resource

Markdown Content:
## 实用资料

浏览全部和风天气开发平台所必备的实用资料，这些是开发文档的必要补充信息，也是开发过程中的扩展工具，例如错误代码的说明、专业名词解释、预警类型、天气图标、城市列表等等。

[专用词汇表](https://dev.qweather.com/docs/resource/glossary/)

和风天气开发者服务涉及到很多专业术语，了解坐标、行政区划、时间格式等专有名词。

[错误码](https://dev.qweather.com/docs/resource/error-code/)

和风天气 API 的错误码和错误信息说明，出现错误的时候，请先参考此文档。

[多语言](https://dev.qweather.com/docs/resource/language/)

和风天气支持30+种主流语言及所在地区的官方语言，查看如何使用多语言查询数据。

[常见城市列表](https://dev.qweather.com/docs/resource/location-list/)

和风天气城市列表，提供兴趣点、景点、环保部国控站点以及全国3000+市县区名称、ID、经纬度等信息。

[元数据](https://dev.qweather.com/docs/resource/metadata/)

介绍 API 返回的 metadata 对象包括哪些信息以及它们的用途。

[单位](https://dev.qweather.com/docs/resource/unit/)

和风天气仅支持公制单位，本篇文档用于介绍和风天气各项要素所支持的单位。

<!-- END_DOC_57 -->

---


<!-- START_DOC_58: terms_restriction.md (URL: https://dev.qweather.com/docs/terms/restriction) -->
Title: 使用限制

URL Source: https://dev.qweather.com/docs/terms/restriction

Published Time: Thu, 27 Aug 2026 02:08:36 GMT

Markdown Content:
为了让开发者更加公平、合规的使用和风天气服务，你需要同意我们的[各项服务条款和限制](https://dev.qweather.com/docs/terms/tos/)。在本篇文档，我们将罗列一些常见的使用限制和解释说明，便与你更好的理解这些条款和限制。

## 不合规的产品[#](https://dev.qweather.com/docs/terms/restriction#non-compliant-products)

你的产品（例如APP或网站等）使用了和风天气的所有服务，该产品必须符合中国法律法规的规定并符合公序良俗，否则你的帐号将被冻结。

## 非正常请求[#](https://dev.qweather.com/docs/terms/restriction#invalid-requests)

当你收到返回数据不是`code=2xx`时，你应该检查你的程序，因为这代表了错误的请求。对于所有错误的请求，如超过合理范围，将被视为对我们服务的攻击，此时你可能无法再继续使用和风天气开发服务或你的帐号将被冻结。

参考[优化请求](https://dev.qweather.com/docs/best-practices/optimize-requests/)和[开发者许可协议2.4.2条款](https://www.qweather.com/terms/developers-eula)。

## 欠款[#](https://dev.qweather.com/docs/terms/restriction#outstanding)

和风天气开发服务的标准订阅采用按量计费后付费模式，你应该按照账单金额支付费用。当账单欠款超过30天的，你的帐号将被冻结，并且我们保留包括采取法律行动等方式继续向你追缴欠款的权利。

参考[账单和计费系统](https://dev.qweather.com/docs/finance/billing-and-payment/)。

## 未注明来源[#](https://dev.qweather.com/docs/terms/restriction#no-attribution)

根据我们的许可和数据源版权要求，你必须在使用了和风天气开发服务的产品中注明来源，否则你可能无法继续使用和风天气任何服务。

参考[注明来源](https://dev.qweather.com/docs/terms/attribution/)。

## 缓存或索引地理信息数据[#](https://dev.qweather.com/docs/terms/restriction#cache-or-index-geoapi-data)

由于地理信息版权方要求以及版权许可限制，当你使用和风天气开发服务中的[地理信息服务](https://dev.qweather.com/docs/api/geoapi/)时，你不能批量缓存或下载这些数据，也不允许通过保存这些地理数据建立索引，否则你将侵犯数据源版权或面临法律风险。

参考[开发者许可协议](https://www.qweather.com/terms/developers-eula)。

## 帐号冻结[#](https://dev.qweather.com/docs/terms/restriction#account-suspension)

当你的帐号被冻结后，你无法使用到任何和风天气开发服务，并且你的支付方式和帐号信息也无法在新的帐号中使用。根据你的违规严重情况，我们有权将你的信息提交给具有管辖权的司法机构。

参考[帐号冻结](https://dev.qweather.com/docs/account/suspension/)。

<!-- END_DOC_58 -->

---


<!-- START_DOC_59: api_tropical-cyclone_storm-list.md (URL: https://dev.qweather.com/docs/api/tropical-cyclone/storm-list) -->
Title: 台风列表

URL Source: https://dev.qweather.com/docs/api/tropical-cyclone/storm-list

Published Time: Thu, 27 Aug 2026 02:08:36 GMT

Markdown Content:
[和风天气开发者服务](https://dev.qweather.com/)

[控制台](https://id.qweather.com/#/login?redirect=https://console.qweather.com)

*   [首页](https://dev.qweather.com/)
*   [文档](https://dev.qweather.com/docs/)
*   [图标](https://icons.qweather.com/)
*   [展示](https://dev.qweather.com/showcase/)
*   [帮助](https://dev.qweather.com/help/)
*   [价格](https://dev.qweather.com/price/)
*   Search K 

*   [文档](https://dev.qweather.com/docs/)
*   >
*   [开发文档](https://dev.qweather.com/docs/api/)
*   >
*   [热带气旋（台风）](https://dev.qweather.com/docs/api/tropical-cyclone/)
*   >
*   [台风列表](https://dev.qweather.com/docs/api/tropical-cyclone/storm-list/)

#### [开始使用](https://dev.qweather.com/docs/start/)

*   [特性](https://dev.qweather.com/docs/features/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [帐号管理](https://dev.qweather.com/docs/account/)

#### [开发文档](https://dev.qweather.com/docs/api/)

*   [GeoAPI](https://dev.qweather.com/docs/api/geoapi/)
*   [天气预报](https://dev.qweather.com/docs/api/weather/)
*   [分钟预报](https://dev.qweather.com/docs/api/minutely/)
*   [预警](https://dev.qweather.com/docs/api/warning/)
*   [天气指数](https://dev.qweather.com/docs/api/indices/)
*   [空气质量](https://dev.qweather.com/docs/api/air-quality/)
*   [时光机](https://dev.qweather.com/docs/api/time-machine/)
*   [热带气旋（台风）](https://dev.qweather.com/docs/api/tropical-cyclone/)

    *   [GET 台风预报](https://dev.qweather.com/docs/api/tropical-cyclone/storm-forecast/)
    *   [GET 台风实况和路径](https://dev.qweather.com/docs/api/tropical-cyclone/storm-track/)
    *   [GET 台风列表](https://dev.qweather.com/docs/api/tropical-cyclone/storm-list/)

*   [海洋数据](https://dev.qweather.com/docs/api/ocean/)
*   [太阳辐射](https://dev.qweather.com/docs/api/solar-radiation/)
*   [天文](https://dev.qweather.com/docs/api/astronomy/)
*   [控制台API](https://dev.qweather.com/docs/api/console/)

#### [实用资料](https://dev.qweather.com/docs/resource/)

*   [专用词汇表](https://dev.qweather.com/docs/resource/glossary/)
*   [错误码](https://dev.qweather.com/docs/resource/error-code/)
*   [多语言](https://dev.qweather.com/docs/resource/language/)
*   [常见城市列表](https://dev.qweather.com/docs/resource/location-list/)
*   [元数据](https://dev.qweather.com/docs/resource/metadata/)
*   [单位](https://dev.qweather.com/docs/resource/unit/)

#### [最佳实践](https://dev.qweather.com/docs/best-practices/)

*   [不要假设](https://dev.qweather.com/docs/best-practices/no-assumptions/)
*   [优化请求](https://dev.qweather.com/docs/best-practices/optimize-requests/)
*   [处理Gzip](https://dev.qweather.com/docs/best-practices/gzip/)
*   [缓存你的数据](https://dev.qweather.com/docs/best-practices/cache/)
*   [安全指南](https://dev.qweather.com/docs/best-practices/security-guidelines/)

#### [财务与费用](https://dev.qweather.com/docs/finance/)

*   [计费方式和支付](https://dev.qweather.com/docs/finance/billing-and-payment/)
*   [按量计费定价](https://dev.qweather.com/docs/finance/pricing/)
*   [节省计划](https://dev.qweather.com/docs/finance/savings-plans/)
*   [增值税发票](https://dev.qweather.com/docs/finance/vat-invoice/)

#### [条款](https://dev.qweather.com/docs/terms/)

*   [使用限制](https://dev.qweather.com/docs/terms/restriction/)
*   [注明来源](https://dev.qweather.com/docs/terms/attribution/)
*   [服务条款](https://dev.qweather.com/docs/terms/tos/)

# 台风列表

台风列表API提供全球主要海洋流域最近2年的台风列表。

> 目前仅支持中国沿海地区，即`basin=NP`

## 请求路径[#](https://dev.qweather.com/docs/api/tropical-cyclone/storm-list#endpoint)

`GET /v7/tropical/storm-list`

## 参数[#](https://dev.qweather.com/docs/api/tropical-cyclone/storm-list#parameters)

#### 查询参数

*   
basin 必选 string  

需要查询的台风所在的流域，例如中国处于西北太平洋，即 `basin=NP`。当前仅支持`NP`。

    *   `AL` North Atlantic 北大西洋
    *   `EP` Eastern Pacific 东太平洋
    *   `NP` NorthWest Pacific 西北太平洋
    *   `SP` SouthWestern Pacific 西南太平洋
    *   `NI` North Indian 北印度洋
    *   `SI` South Indian 南印度洋

*   year 必选 number  支持查询本年度和上一年度的台风，例如：`year=2020`, `year=2019`  

## 请求示例[#](https://dev.qweather.com/docs/api/tropical-cyclone/storm-list#request-example)

```bash
curl -X GET --compressed \
-H 'Authorization: Bearer your_token' \
'https://your-api-host/v7/tropical/storm-list?basin=NP&year=2023'
```

请将`your_token`替换为你的[JWT身份认证](https://dev.qweather.com/docs/configuration/authentication/)，将`your_api_host`替换为你的[API Host](https://dev.qweather.com/docs/configuration/api-host/)

[试一下](https://dev.qweather.com/api-explore/#/Storm/getStormList)

## 返回数据[#](https://dev.qweather.com/docs/api/tropical-cyclone/storm-list#response)

```json
{
  "code": "200",
  "updateTime": "2020-12-31T16:00+00:00",
  "fxLink": "https://www.qweather.com",
  "storm": [
    {
      "id": "NP_2022",
      "name": "环高",
      "basin": "NP",
      "year": "2020",
      "isActive": "0"
    },
    {
      "id": "NP_2021",
      "name": "艾涛",
      "basin": "NP",
      "year": "2020",
      "isActive": "0"
    },
    {
      "id": "NP_2020",
      "name": "艾莎尼",
      "basin": "NP",
      "year": "2020",
      "isActive": "0"
    },
    {
      "id": "NP_2019",
      "name": "天鹅",
      "basin": "NP",
      "year": "2020",
      "isActive": "0"
    },
    {
      "id": "NP_2018",
      "name": "莫拉菲",
      "basin": "NP",
      "year": "2020",
      "isActive": "0"
    },
    {
      "id": "NP_2017",
      "name": "沙德尔",
      "basin": "NP",
      "year": "2020",
      "isActive": "0"
    },
    {
      "id": "NP_200012",
      "name": "无命名",
      "basin": "NP",
      "year": "2020",
      "isActive": "0"
    },
    {
      "id": "NP_2016",
      "name": "浪卡",
      "basin": "NP",
      "year": "2020",
      "isActive": "0"
    },
    {
      "id": "NP_2015",
      "name": "莲花",
      "basin": "NP",
      "year": "2020",
      "isActive": "0"
    },
    {
      "id": "NP_2014",
      "name": "灿鸿",
      "basin": "NP",
      "year": "2020",
      "isActive": "0"
    },
    {
      "id": "NP_2013",
      "name": "鲸鱼",
      "basin": "NP",
      "year": "2020",
      "isActive": "0"
    },
    {
      "id": "NP_2012",
      "name": "白海豚",
      "basin": "NP",
      "year": "2020",
      "isActive": "0"
    },
    {
      "id": "NP_2011",
      "name": "红霞",
      "basin": "NP",
      "year": "2020",
      "isActive": "0"
    },
    {
      "id": "NP_2010",
      "name": "海神",
      "basin": "NP",
      "year": "2020",
      "isActive": "0"
    },
    {
      "id": "NP_2009",
      "name": "美莎克",
      "basin": "NP",
      "year": "2020",
      "isActive": "0"
    },
    {
      "id": "NP_2008",
      "name": "巴威",
      "basin": "NP",
      "year": "2020",
      "isActive": "0"
    },
    {
      "id": "NP_2007",
      "name": "海高斯",
      "basin": "NP",
      "year": "2020",
      "isActive": "0"
    },
    {
      "id": "NP_2006",
      "name": "米克拉",
      "basin": "NP",
      "year": "2020",
      "isActive": "0"
    },
    {
      "id": "NP_2005",
      "name": "蔷薇",
      "basin": "NP",
      "year": "2020",
      "isActive": "0"
    },
    {
      "id": "NP_2004",
      "name": "黑格比",
      "basin": "NP",
      "year": "2020",
      "isActive": "0"
    },
    {
      "id": "NP_2003",
      "name": "森拉克",
      "basin": "NP",
      "year": "2020",
      "isActive": "0"
    },
    {
      "id": "NP_2002",
      "name": "鹦鹉",
      "basin": "NP",
      "year": "2020",
      "isActive": "0"
    },
    {
      "id": "NP_2001",
      "name": "黄蜂",
      "basin": "NP",
      "year": "2020",
      "isActive": "0"
    }
  ],
  "refer": {
    "sources": [
      "https://developer.qweather.com/attribution.html"
    ],
    "license": [
      "QWeather Developers License"
    ]
  }
}
```

*   code string  [状态码](https://dev.qweather.com/docs/resource/error-code/)  
*   updateTime date-time  [API的最近更新时间](https://dev.qweather.com/docs/resource/glossary/#update-time)  
*   fxLink uri  当前数据的响应式页面，便于嵌入网站或应用  
*   storm object  台风列表  
    *   id string  台风ID  
    *   name string  台风名称  
    *   basin string  台风所处流域  
    *   year string  台风所处年份  
    *   isActive string  是否为活跃台风。`1` 活跃台风，`0` 停编  

*   refer object  数据来源和许可信息  
    *   sources array  原始数据来源，或数据源说明，可能为空  

    *   license array  数据许可或版权声明，可能为空  

#### 本页导航

*   [请求路径](https://dev.qweather.com/docs/api/tropical-cyclone/storm-list#endpoint)
*   [参数](https://dev.qweather.com/docs/api/tropical-cyclone/storm-list#parameters)
*   [请求示例](https://dev.qweather.com/docs/api/tropical-cyclone/storm-list#request-example)
*   [返回数据](https://dev.qweather.com/docs/api/tropical-cyclone/storm-list#response)

### [和风天气开发者服务](https://dev.qweather.com/)

[联系](https://www.qweather.com/contact/)• [博客](https://blog.qweather.com/)• [GitHub](https://github.com/qwd)

Language 

### 文档

*   [开始使用](https://dev.qweather.com/docs/start/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [开发文档](https://dev.qweather.com/docs/api/)
*   [财务与费用](https://dev.qweather.com/docs/finance/)
*   [最佳实践](https://dev.qweather.com/docs/best-practices/)
*   [实用资料](https://dev.qweather.com/docs/resource/)

### 特性

*   [服务和数据](https://dev.qweather.com/docs/features/service-and-data/)
*   [全球部署](https://dev.qweather.com/docs/features/global-deployment/)
*   [性能](https://dev.qweather.com/docs/features/performance/)

### 更多

*   [天气预报](https://www.qweather.com/)
*   [天气地图](https://map.qweather.com/)
*   [下载APP](https://www.qweather.com/app/)

*   © 2026 [QWeather](https://www.qweather.com/en/)
*   [许可协议](https://www.qweather.com/terms/developers-eula/)
*   [隐私政策](https://www.qweather.com/terms/privacy)
*   [免责声明](https://www.qweather.com/terms/disclaimer/)

[京ICP备15048401号-11](https://beian.miit.gov.cn/)

<!-- END_DOC_59 -->

---


<!-- START_DOC_60: api_tropical-cyclone_storm-track.md (URL: https://dev.qweather.com/docs/api/tropical-cyclone/storm-track) -->
Title: 台风实况和路径

URL Source: https://dev.qweather.com/docs/api/tropical-cyclone/storm-track

Markdown Content:
## 台风实况和路径

台风实况和路径API提供全球主要海洋流域的台风实时位置、等级、气压、风速以及活跃台风的轨迹路径。 
## 请求路径[#](https://dev.qweather.com/docs/api/tropical-cyclone/storm-track#endpoint)

`GET /v7/tropical/storm-track`

## 参数[#](https://dev.qweather.com/docs/api/tropical-cyclone/storm-track#parameters)

#### 查询参数

*   stormid 必选

string 
需要查询的台风ID，StormID可通过[台风查询API](https://dev.qweather.com/docs/api/tropical-cyclone/storm-list/)获取。例如 `stormid=NP2018` 

## 请求示例[#](https://dev.qweather.com/docs/api/tropical-cyclone/storm-track#request-example)

```
curl -X GET --compressed \
-H 'Authorization: Bearer your_token' \
'https://your-api-host/v7/tropical/storm-track?stormid=NP2018'
```

请将`your_token`替换为你的[JWT身份认证](https://dev.qweather.com/docs/configuration/authentication/)，将`your_api_host`替换为你的[API Host](https://dev.qweather.com/docs/configuration/api-host/)

[试一下](https://dev.qweather.com/api-explore/#/Storm/getStormTrack)

## 返回数据[#](https://dev.qweather.com/docs/api/tropical-cyclone/storm-track#response)

*   code

string 
*   updateTime

date-time 
*   fxLink

uri 
当前数据的响应式页面，便于嵌入网站或应用 
*   isActive

string 
是否为活跃台风。`1` 活跃台风，`0` 停编 
*   now

object 
当前台风数据。如台风已结束，该字段可为 null 
    *   pubTime

date-time 
原始数据发布时间 
    *   lat

string 
台风所处纬度 
    *   lon

string 
台风所处经度 
    *   type

string 
台风类型 
    *   pressure

string 
台风中心气压 
    *   windSpeed

string 
台风附近最大风速 
    *   moveSpeed

string 
台风移动速度 
    *   moveDir

string 
台风移动方位 
    *   move360

string 
台风移动方位360度方向 
    *   windRadius30

object 
台风7级风圈半径 
        *   neRadius

string 
台风7级风圈东北半径，可能为空 
        *   seRadius

string 
当前台风7级风圈东南半径，可能为空 
        *   swRadius

string 
当前台风7级风圈西南半径，可能为空 
        *   nwRadius

string 
当前台风7级风圈西北半径，可能为空 

    *   windRadius50

object 
台风10级风圈半径 
        *   neRadius

string 
台风10级风圈东北半径，可能为空 
        *   seRadius

string 
当前台风10级风圈东南半径，可能为空 
        *   swRadius

string 
当前台风10级风圈西南半径，可能为空 
        *   nwRadius

string 
当前台风10级风圈西北半径，可能为空 

    *   windRadius64

object 
台风12级风圈半径 
        *   neRadius

string 
台风12级风圈东北半径，可能为空 
        *   seRadius

string 
当前台风12级风圈东南半径，可能为空 
        *   swRadius

string 
当前台风12级风圈西南半径，可能为空 
        *   nwRadius

string 
当前台风12级风圈西北半径，可能为空 

*   track

array 
台风轨迹数据列表 
    *   time

date-time 
当前台风信息发布时间 
    *   lat

string 
台风所处纬度 
    *   lon

string 
台风所处经度 
    *   type

string 
台风类型 
    *   pressure

string 
台风中心气压 
    *   windSpeed

string 
台风附近最大风速 
    *   moveSpeed

string 
台风移动速度 
    *   moveDir

string 
台风移动方位 
    *   move360

string 
台风移动方位360度方向 
    *   windRadius30

object 
台风7级风圈半径 
        *   neRadius

string 
台风7级风圈东北半径，可能为空 
        *   seRadius

string 
当前台风7级风圈东南半径，可能为空 
        *   swRadius

string 
当前台风7级风圈西南半径，可能为空 
        *   nwRadius

string 
当前台风7级风圈西北半径，可能为空 

    *   windRadius50

object 
台风10级风圈半径 
        *   neRadius

string 
台风10级风圈东北半径，可能为空 
        *   seRadius

string 
当前台风10级风圈东南半径，可能为空 
        *   swRadius

string 
当前台风10级风圈西南半径，可能为空 
        *   nwRadius

string 
当前台风10级风圈西北半径，可能为空 

    *   windRadius64

object 
台风12级风圈半径 
        *   neRadius

string 
台风12级风圈东北半径，可能为空 
        *   seRadius

string 
当前台风12级风圈东南半径，可能为空 
        *   swRadius

string 
当前台风12级风圈西南半径，可能为空 
        *   nwRadius

string 
当前台风12级风圈西北半径，可能为空 

*   refer

object 
数据来源和许可信息 
    *   sources

array 
原始数据来源，或数据源说明，可能为空 

    *   license

array 
数据许可或版权声明，可能为空

<!-- END_DOC_60 -->

---


<!-- START_DOC_61: api_tropical-cyclone_storm-forecast.md (URL: https://dev.qweather.com/docs/api/tropical-cyclone/storm-forecast) -->
Title: 台风预报

URL Source: https://dev.qweather.com/docs/api/tropical-cyclone/storm-forecast

Markdown Content:
[和风天气开发者服务](https://dev.qweather.com/)

[控制台](https://id.qweather.com/#/login?redirect=https://console.qweather.com)

*   [首页](https://dev.qweather.com/)
*   [文档](https://dev.qweather.com/docs/)
*   [图标](https://icons.qweather.com/)
*   [展示](https://dev.qweather.com/showcase/)
*   [帮助](https://dev.qweather.com/help/)
*   [价格](https://dev.qweather.com/price/)
*   Search K 

*   [文档](https://dev.qweather.com/docs/)
*   >
*   [开发文档](https://dev.qweather.com/docs/api/)
*   >
*   [热带气旋（台风）](https://dev.qweather.com/docs/api/tropical-cyclone/)
*   >
*   [台风预报](https://dev.qweather.com/docs/api/tropical-cyclone/storm-forecast/)

#### [开始使用](https://dev.qweather.com/docs/start/)

*   [特性](https://dev.qweather.com/docs/features/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [帐号管理](https://dev.qweather.com/docs/account/)

#### [开发文档](https://dev.qweather.com/docs/api/)

*   [GeoAPI](https://dev.qweather.com/docs/api/geoapi/)
*   [天气预报](https://dev.qweather.com/docs/api/weather/)
*   [分钟预报](https://dev.qweather.com/docs/api/minutely/)
*   [预警](https://dev.qweather.com/docs/api/warning/)
*   [天气指数](https://dev.qweather.com/docs/api/indices/)
*   [空气质量](https://dev.qweather.com/docs/api/air-quality/)
*   [时光机](https://dev.qweather.com/docs/api/time-machine/)
*   [热带气旋（台风）](https://dev.qweather.com/docs/api/tropical-cyclone/)

    *   [GET 台风预报](https://dev.qweather.com/docs/api/tropical-cyclone/storm-forecast/)
    *   [GET 台风实况和路径](https://dev.qweather.com/docs/api/tropical-cyclone/storm-track/)
    *   [GET 台风列表](https://dev.qweather.com/docs/api/tropical-cyclone/storm-list/)

*   [海洋数据](https://dev.qweather.com/docs/api/ocean/)
*   [太阳辐射](https://dev.qweather.com/docs/api/solar-radiation/)
*   [天文](https://dev.qweather.com/docs/api/astronomy/)
*   [控制台API](https://dev.qweather.com/docs/api/console/)

#### [实用资料](https://dev.qweather.com/docs/resource/)

*   [专用词汇表](https://dev.qweather.com/docs/resource/glossary/)
*   [错误码](https://dev.qweather.com/docs/resource/error-code/)
*   [多语言](https://dev.qweather.com/docs/resource/language/)
*   [常见城市列表](https://dev.qweather.com/docs/resource/location-list/)
*   [元数据](https://dev.qweather.com/docs/resource/metadata/)
*   [单位](https://dev.qweather.com/docs/resource/unit/)

#### [最佳实践](https://dev.qweather.com/docs/best-practices/)

*   [不要假设](https://dev.qweather.com/docs/best-practices/no-assumptions/)
*   [优化请求](https://dev.qweather.com/docs/best-practices/optimize-requests/)
*   [处理Gzip](https://dev.qweather.com/docs/best-practices/gzip/)
*   [缓存你的数据](https://dev.qweather.com/docs/best-practices/cache/)
*   [安全指南](https://dev.qweather.com/docs/best-practices/security-guidelines/)

#### [财务与费用](https://dev.qweather.com/docs/finance/)

*   [计费方式和支付](https://dev.qweather.com/docs/finance/billing-and-payment/)
*   [按量计费定价](https://dev.qweather.com/docs/finance/pricing/)
*   [节省计划](https://dev.qweather.com/docs/finance/savings-plans/)
*   [增值税发票](https://dev.qweather.com/docs/finance/vat-invoice/)

#### [条款](https://dev.qweather.com/docs/terms/)

*   [使用限制](https://dev.qweather.com/docs/terms/restriction/)
*   [注明来源](https://dev.qweather.com/docs/terms/attribution/)
*   [服务条款](https://dev.qweather.com/docs/terms/tos/)

# 台风预报

台风预报API提供全球主要海洋流域的台风预测位置、等级、气压、风速等。

> 如果查询的台风已经结束，则返回的数据为空，建议先通过[台风列表接口](https://dev.qweather.com/docs/api/tropical-cyclone/storm-list/)获取台风的状态

## 请求路径[#](https://dev.qweather.com/docs/api/tropical-cyclone/storm-forecast#endpoint)

`GET /v7/tropical/storm-forecast`

## 参数[#](https://dev.qweather.com/docs/api/tropical-cyclone/storm-forecast#parameters)

#### 查询参数

*   stormid 必选 string  需要查询的台风ID，StormID可通过[台风查询API](https://dev.qweather.com/docs/api/tropical-cyclone/storm-list/)获取。例如 `stormid=NP2018`  

## 请求示例[#](https://dev.qweather.com/docs/api/tropical-cyclone/storm-forecast#request-example)

```bash
curl -X GET --compressed \
-H 'Authorization: Bearer your_token' \
'https://your-api-host/v7/tropical/storm-forecast?stormid=NP2018'
```

请将`your_token`替换为你的[JWT身份认证](https://dev.qweather.com/docs/configuration/authentication/)，将`your_api_host`替换为你的[API Host](https://dev.qweather.com/docs/configuration/api-host/)

[试一下](https://dev.qweather.com/api-explore/#/Storm/getStormForecast)

## 返回数据[#](https://dev.qweather.com/docs/api/tropical-cyclone/storm-forecast#response)

```json
{
  "code": "200",
  "updateTime": "2021-07-27T03:00+00:00",
  "fxLink": "https://www.qweather.com",
  "forecast": [
    {
      "fxTime": "2021-07-27T20:00+08:00",
      "lat": "31.7",
      "lon": "118.4",
      "type": "TS",
      "pressure": "990",
      "windSpeed": "18",
      "moveSpeed": "",
      "moveDir": "",
      "move360": ""
    },
    {
      "fxTime": "2021-07-28T08:00+08:00",
      "lat": "32.5",
      "lon": "117.4",
      "type": "TD",
      "pressure": "992",
      "windSpeed": "15",
      "moveSpeed": "",
      "moveDir": "",
      "move360": ""
    },
    {
      "fxTime": "2021-07-28T20:00+08:00",
      "lat": "33.1",
      "lon": "117.2",
      "type": "TD",
      "pressure": "992",
      "windSpeed": "15",
      "moveSpeed": "",
      "moveDir": "",
      "move360": ""
    },
    {
      "fxTime": "2021-07-29T08:00+08:00",
      "lat": "34.3",
      "lon": "117.2",
      "type": "TD",
      "pressure": "992",
      "windSpeed": "15",
      "moveSpeed": "",
      "moveDir": "",
      "move360": ""
    },
    {
      "fxTime": "2021-07-29T20:00+08:00",
      "lat": "36",
      "lon": "117.8",
      "type": "TD",
      "pressure": "992",
      "windSpeed": "15",
      "moveSpeed": "",
      "moveDir": "",
      "move360": ""
    },
    {
      "fxTime": "2021-07-30T08:00+08:00",
      "lat": "37.1",
      "lon": "118.7",
      "type": "TD",
      "pressure": "995",
      "windSpeed": "15",
      "moveSpeed": "",
      "moveDir": "",
      "move360": ""
    },
    {
      "fxTime": "2021-07-31T08:00+08:00",
      "lat": "38",
      "lon": "119.8",
      "type": "TD",
      "pressure": "995",
      "windSpeed": "15",
      "moveSpeed": "",
      "moveDir": "",
      "move360": ""
    }
  ],
  "refer": {
    "sources": [
      "https://developer.qweather.com/attribution.html"
    ],
    "license": [
      "QWeather Developers License"
    ]
  }
}
```

*   code string  [状态码](https://dev.qweather.com/docs/resource/error-code/)  
*   updateTime date-time  [API的最近更新时间](https://dev.qweather.com/docs/resource/glossary/#update-time)  
*   fxLink uri  当前数据的响应式页面，便于嵌入网站或应用  
*   forecast array  台风预报数据列表  
    *   fxTime date-time  预报时间  
    *   lat string  台风所处纬度  
    *   lon string  台风所处经度  
    *   type string  台风类型  
    *   pressure string  台风中心气压  
    *   windSpeed string  台风附近最大风速  
    *   moveSpeed string  台风移动速度  
    *   moveDir string  台风移动方位  
    *   move360 string  台风移动方位360度方向  

*   refer object  数据来源和许可信息  
    *   sources array  原始数据来源，或数据源说明，可能为空  

    *   license array  数据许可或版权声明，可能为空  

#### 本页导航

*   [请求路径](https://dev.qweather.com/docs/api/tropical-cyclone/storm-forecast#endpoint)
*   [参数](https://dev.qweather.com/docs/api/tropical-cyclone/storm-forecast#parameters)
*   [请求示例](https://dev.qweather.com/docs/api/tropical-cyclone/storm-forecast#request-example)
*   [返回数据](https://dev.qweather.com/docs/api/tropical-cyclone/storm-forecast#response)

### [和风天气开发者服务](https://dev.qweather.com/)

[联系](https://www.qweather.com/contact/)• [博客](https://blog.qweather.com/)• [GitHub](https://github.com/qwd)

Language 

### 文档

*   [开始使用](https://dev.qweather.com/docs/start/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [开发文档](https://dev.qweather.com/docs/api/)
*   [财务与费用](https://dev.qweather.com/docs/finance/)
*   [最佳实践](https://dev.qweather.com/docs/best-practices/)
*   [实用资料](https://dev.qweather.com/docs/resource/)

### 特性

*   [服务和数据](https://dev.qweather.com/docs/features/service-and-data/)
*   [全球部署](https://dev.qweather.com/docs/features/global-deployment/)
*   [性能](https://dev.qweather.com/docs/features/performance/)

### 更多

*   [天气预报](https://www.qweather.com/)
*   [天气地图](https://map.qweather.com/)
*   [下载APP](https://www.qweather.com/app/)

*   © 2026 [QWeather](https://www.qweather.com/en/)
*   [许可协议](https://www.qweather.com/terms/developers-eula/)
*   [隐私政策](https://www.qweather.com/terms/privacy)
*   [免责声明](https://www.qweather.com/terms/disclaimer/)

[京ICP备15048401号-11](https://beian.miit.gov.cn/)

<!-- END_DOC_61 -->

---


<!-- START_DOC_62: api_solar-radiation.md (URL: https://dev.qweather.com/docs/api/solar-radiation) -->
Title: 太阳辐射

URL Source: https://dev.qweather.com/docs/api/solar-radiation

Markdown Content:
[和风天气开发者服务](https://dev.qweather.com/)

[控制台](https://id.qweather.com/#/login?redirect=https://console.qweather.com)

*   [首页](https://dev.qweather.com/)
*   [文档](https://dev.qweather.com/docs/)
*   [图标](https://icons.qweather.com/)
*   [展示](https://dev.qweather.com/showcase/)
*   [帮助](https://dev.qweather.com/help/)
*   [价格](https://dev.qweather.com/price/)
*   Search K 

*   [文档](https://dev.qweather.com/docs/)
*   >
*   [开发文档](https://dev.qweather.com/docs/api/)
*   >
*   [太阳辐射](https://dev.qweather.com/docs/api/solar-radiation/)

#### [开始使用](https://dev.qweather.com/docs/start/)

*   [特性](https://dev.qweather.com/docs/features/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [帐号管理](https://dev.qweather.com/docs/account/)

#### [开发文档](https://dev.qweather.com/docs/api/)

*   [GeoAPI](https://dev.qweather.com/docs/api/geoapi/)
*   [天气预报](https://dev.qweather.com/docs/api/weather/)
*   [分钟预报](https://dev.qweather.com/docs/api/minutely/)
*   [预警](https://dev.qweather.com/docs/api/warning/)
*   [天气指数](https://dev.qweather.com/docs/api/indices/)
*   [空气质量](https://dev.qweather.com/docs/api/air-quality/)
*   [时光机](https://dev.qweather.com/docs/api/time-machine/)
*   [热带气旋（台风）](https://dev.qweather.com/docs/api/tropical-cyclone/)
*   [海洋数据](https://dev.qweather.com/docs/api/ocean/)
*   [太阳辐射](https://dev.qweather.com/docs/api/solar-radiation/)

    *   [GET 太阳辐射预报](https://dev.qweather.com/docs/api/solar-radiation/solar-radiation-forecast/)

*   [天文](https://dev.qweather.com/docs/api/astronomy/)
*   [控制台API](https://dev.qweather.com/docs/api/console/)

#### [实用资料](https://dev.qweather.com/docs/resource/)

*   [专用词汇表](https://dev.qweather.com/docs/resource/glossary/)
*   [错误码](https://dev.qweather.com/docs/resource/error-code/)
*   [多语言](https://dev.qweather.com/docs/resource/language/)
*   [常见城市列表](https://dev.qweather.com/docs/resource/location-list/)
*   [元数据](https://dev.qweather.com/docs/resource/metadata/)
*   [单位](https://dev.qweather.com/docs/resource/unit/)

#### [最佳实践](https://dev.qweather.com/docs/best-practices/)

*   [不要假设](https://dev.qweather.com/docs/best-practices/no-assumptions/)
*   [优化请求](https://dev.qweather.com/docs/best-practices/optimize-requests/)
*   [处理Gzip](https://dev.qweather.com/docs/best-practices/gzip/)
*   [缓存你的数据](https://dev.qweather.com/docs/best-practices/cache/)
*   [安全指南](https://dev.qweather.com/docs/best-practices/security-guidelines/)

#### [财务与费用](https://dev.qweather.com/docs/finance/)

*   [计费方式和支付](https://dev.qweather.com/docs/finance/billing-and-payment/)
*   [按量计费定价](https://dev.qweather.com/docs/finance/pricing/)
*   [节省计划](https://dev.qweather.com/docs/finance/savings-plans/)
*   [增值税发票](https://dev.qweather.com/docs/finance/vat-invoice/)

#### [条款](https://dev.qweather.com/docs/terms/)

*   [使用限制](https://dev.qweather.com/docs/terms/restriction/)
*   [注明来源](https://dev.qweather.com/docs/terms/attribution/)
*   [服务条款](https://dev.qweather.com/docs/terms/tos/)

# 太阳辐射

太阳辐射 API 提供全球辐射数据，包括DNI、DHI、GHI以及相关气象数据，15分钟间隔，1公里分辨率。

API

[GET 太阳辐射预报](https://dev.qweather.com/docs/api/solar-radiation/solar-radiation-forecast/)
获取全球任意坐标的逐15分钟太阳辐射预报及相关数据，最多支持未来60小时预报，分辨率为1x1公里。

### [和风天气开发者服务](https://dev.qweather.com/)

[联系](https://www.qweather.com/contact/)• [博客](https://blog.qweather.com/)• [GitHub](https://github.com/qwd)

Language 

### 文档

*   [开始使用](https://dev.qweather.com/docs/start/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [开发文档](https://dev.qweather.com/docs/api/)
*   [财务与费用](https://dev.qweather.com/docs/finance/)
*   [最佳实践](https://dev.qweather.com/docs/best-practices/)
*   [实用资料](https://dev.qweather.com/docs/resource/)

### 特性

*   [服务和数据](https://dev.qweather.com/docs/features/service-and-data/)
*   [全球部署](https://dev.qweather.com/docs/features/global-deployment/)
*   [性能](https://dev.qweather.com/docs/features/performance/)

### 更多

*   [天气预报](https://www.qweather.com/)
*   [天气地图](https://map.qweather.com/)
*   [下载APP](https://www.qweather.com/app/)

*   © 2026 [QWeather](https://www.qweather.com/en/)
*   [许可协议](https://www.qweather.com/terms/developers-eula/)
*   [隐私政策](https://www.qweather.com/terms/privacy)
*   [免责声明](https://www.qweather.com/terms/disclaimer/)

[京ICP备15048401号-11](https://beian.miit.gov.cn/)

<!-- END_DOC_62 -->

---


<!-- START_DOC_63: api_solar-radiation_solar-radiation-forecast.md (URL: https://dev.qweather.com/docs/api/solar-radiation/solar-radiation-forecast) -->
Title: 太阳辐射预报

URL Source: https://dev.qweather.com/docs/api/solar-radiation/solar-radiation-forecast

Markdown Content:
## 太阳辐射预报

获取全球任意坐标的逐15分钟太阳辐射预报及相关数据，最多支持未来60小时预报，分辨率为1x1公里。 
## 请求路径[#](https://dev.qweather.com/docs/api/solar-radiation/solar-radiation-forecast#endpoint)

`GET /solarradiation/v1/forecast/{latitude}/{longitude}`

## 参数[#](https://dev.qweather.com/docs/api/solar-radiation/solar-radiation-forecast#parameters)

#### 路径参数

*   latitude 必选

number 
所需位置的纬度。十进制，最多支持小数点后两位。例如 `39.92` 
*   longitude 必选

number 
所需位置的经度。十进制，最多支持小数点后两位。例如 `116.41` 

#### 查询参数

*   hours

integer 
预报小时数，可选1-60，默认`24`。例如： `hours=12` 
*   interval

integer 
预报数据时间间隔，可选`15`、`30`、`60`分钟，默认`60`。例如：`interval=15` 
*   tilt

integer 
光伏系统的倾斜角度，可选0-90，整数。当`extra=poa`时该参数必传。例如：`tilt=30` 
*   azimuth

integer 
光伏系统的方位角，可选0-359，整数，0 = 北。当`extra=poa`时该参数必传。例如：`azimuth=180` 
*   extra

string 
为当前太阳辐照预报提供的额外信息，可选`weather`（基本天气数据）和`poa`（Plane of array，阵列平面辐照度，必须同时传递`tilt`和`azimuth`），多项使用英文逗号分割。例如：`extra=weather` 
*   localTime

boolean 是否返回查询地点的本地时间

可选值： `false` UTC时间（默认），`true` 本地时间  

## 请求示例[#](https://dev.qweather.com/docs/api/solar-radiation/solar-radiation-forecast#request-example)

```
curl -X GET --compressed \
-H 'Authorization: Bearer your_token' \
'https://your-api-host/solarradiation/v1/forecast/39.92/116.41'
```

请将`your_token`替换为你的[JWT身份认证](https://dev.qweather.com/docs/configuration/authentication/)，将`your_api_host`替换为你的[API Host](https://dev.qweather.com/docs/configuration/api-host/)

[试一下](https://dev.qweather.com/api-explore/#/Solar%20Irradiation/getSolarradiationForecast)

## 返回数据[#](https://dev.qweather.com/docs/api/solar-radiation/solar-radiation-forecast#response)

*   metadata

object 
    *   tag

string 
数据唯一标识 
    *   attributions

array 
数据归因信息或声明，必须与当前数据共同显示 

*   forecasts

array 
太阳辐照预报数据列表 
    *   forecastTime

date-time 
预报时间 
    *   solarAngle

object 
太阳角度 
        *   azimuth

number 
太阳方位角，正北为0度 
        *   elevation

number 
太阳高度角 

    *   dni

object 
法向直接辐照 
        *   value

number 
数值 
        *   unit

string 

    *   dhi

object 
散射水平面辐照 
        *   value

number 
数值 
        *   unit

string 

    *   ghi

object 
总水平面辐照 
        *   value

number 
数值 
        *   unit

string 

    *   weather

object 
基本天气数据，仅在 `extra=weather` 时返回 
        *   temperature

object 
温度 
            *   value

number 
数值 
            *   unit

string 

        *   windSpeed

object 
风速 
            *   value

number 
数值 
            *   unit

string 

        *   humidity

number 
相对湿度 

    *   poa

object 
阵列平面辐照度，仅在 `extra=poa` 时返回 
        *   global

object 
阵列平面总辐照度 
            *   value

number 
数值 
            *   unit

string 

        *   direct

object 
阵列平面直接辐照度 
            *   value

number 
数值 
            *   unit

string 

        *   diffuse

object 
阵列平面散射辐照度 
            *   value

number 
数值 
            *   unit

string 

        *   reflected

object 
阵列平面反射辐照度 
            *   value

number 
数值 
            *   unit

string

<!-- END_DOC_63 -->

---


<!-- START_DOC_64: api_astronomy_solar-elevation-angle.md (URL: https://dev.qweather.com/docs/api/astronomy/solar-elevation-angle) -->
Title: 太阳高度角

URL Source: https://dev.qweather.com/docs/api/astronomy/solar-elevation-angle

Markdown Content:
## 太阳高度角

任意时间点的全球太阳高度及方位角。 
## 请求路径[#](https://dev.qweather.com/docs/api/astronomy/solar-elevation-angle#endpoint)

`GET /v7/astronomy/solar-elevation-angle`

## 参数[#](https://dev.qweather.com/docs/api/astronomy/solar-elevation-angle#parameters)

#### 查询参数

*   location 必选

string 
需要查询地区的以英文逗号分隔的[经度,纬度坐标](https://dev.qweather.com/docs/resource/glossary/#coordinate)（十进制，最多支持小数点后两位）。例如 `location=116.41,39.92` 
*   date 必选

date 
查询日期，格式为yyyyMMdd，例如 `date=20170809` 
*   time 必选

string 
查询时间，格式为HHmm，24时制，例如 `time=1230` 
*   tz 必选

string 
查询地区所在时区，例如`tz=0800`或`tz=-0530` 
*   alt 必选

number 
海拔高度，单位为米，例如`alt=43` 

## 请求示例[#](https://dev.qweather.com/docs/api/astronomy/solar-elevation-angle#request-example)

```
curl -X GET --compressed \
-H 'Authorization: Bearer your_token' \
'https://your-api-host/v7/astronomy/solar-elevation-angle?location=116.41%2C39.92&date=20180531&time=1230&tz=0800&alt=43'
```

请将`your_token`替换为你的[JWT身份认证](https://dev.qweather.com/docs/configuration/authentication/)，将`your_api_host`替换为你的[API Host](https://dev.qweather.com/docs/configuration/api-host/)

[试一下](https://dev.qweather.com/api-explore/#/Astronomy/getAstronomySolarElevationAngle)

## 返回数据[#](https://dev.qweather.com/docs/api/astronomy/solar-elevation-angle#response)

*   code

string 
*   solarElevationAngle

string 
太阳高度角 
*   solarAzimuthAngle

string 
太阳方位角，0度为正北 
*   solarHour

string 
太阳时，HHmm格式 
*   hourAngle

string 
时角 
*   refer

object 
数据来源和许可信息 
    *   sources

array 
原始数据来源，或数据源说明，可能为空 

    *   license

array 
数据许可或版权声明，可能为空

<!-- END_DOC_64 -->

---


<!-- START_DOC_65: features.md (URL: https://dev.qweather.com/docs/features) -->
Title: 特性

URL Source: https://dev.qweather.com/docs/features

Markdown Content:
[和风天气开发者服务](https://dev.qweather.com/)

[控制台](https://id.qweather.com/#/login?redirect=https://console.qweather.com)

*   [首页](https://dev.qweather.com/)
*   [文档](https://dev.qweather.com/docs/)
*   [图标](https://icons.qweather.com/)
*   [展示](https://dev.qweather.com/showcase/)
*   [帮助](https://dev.qweather.com/help/)
*   [价格](https://dev.qweather.com/price/)
*   Search K 

*   [文档](https://dev.qweather.com/docs/)
*   >
*   [特性](https://dev.qweather.com/docs/features/)

#### [开始使用](https://dev.qweather.com/docs/start/)

*   [特性](https://dev.qweather.com/docs/features/)

    *   [服务和数据](https://dev.qweather.com/docs/features/service-and-data/)
    *   [全球部署](https://dev.qweather.com/docs/features/global-deployment/)
    *   [性能](https://dev.qweather.com/docs/features/performance/)

*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [帐号管理](https://dev.qweather.com/docs/account/)

#### [开发文档](https://dev.qweather.com/docs/api/)

*   [GeoAPI](https://dev.qweather.com/docs/api/geoapi/)
*   [天气预报](https://dev.qweather.com/docs/api/weather/)
*   [分钟预报](https://dev.qweather.com/docs/api/minutely/)
*   [预警](https://dev.qweather.com/docs/api/warning/)
*   [天气指数](https://dev.qweather.com/docs/api/indices/)
*   [空气质量](https://dev.qweather.com/docs/api/air-quality/)
*   [时光机](https://dev.qweather.com/docs/api/time-machine/)
*   [热带气旋（台风）](https://dev.qweather.com/docs/api/tropical-cyclone/)
*   [海洋数据](https://dev.qweather.com/docs/api/ocean/)
*   [太阳辐射](https://dev.qweather.com/docs/api/solar-radiation/)
*   [天文](https://dev.qweather.com/docs/api/astronomy/)
*   [控制台API](https://dev.qweather.com/docs/api/console/)

#### [实用资料](https://dev.qweather.com/docs/resource/)

*   [专用词汇表](https://dev.qweather.com/docs/resource/glossary/)
*   [错误码](https://dev.qweather.com/docs/resource/error-code/)
*   [多语言](https://dev.qweather.com/docs/resource/language/)
*   [常见城市列表](https://dev.qweather.com/docs/resource/location-list/)
*   [元数据](https://dev.qweather.com/docs/resource/metadata/)
*   [单位](https://dev.qweather.com/docs/resource/unit/)

#### [最佳实践](https://dev.qweather.com/docs/best-practices/)

*   [不要假设](https://dev.qweather.com/docs/best-practices/no-assumptions/)
*   [优化请求](https://dev.qweather.com/docs/best-practices/optimize-requests/)
*   [处理Gzip](https://dev.qweather.com/docs/best-practices/gzip/)
*   [缓存你的数据](https://dev.qweather.com/docs/best-practices/cache/)
*   [安全指南](https://dev.qweather.com/docs/best-practices/security-guidelines/)

#### [财务与费用](https://dev.qweather.com/docs/finance/)

*   [计费方式和支付](https://dev.qweather.com/docs/finance/billing-and-payment/)
*   [按量计费定价](https://dev.qweather.com/docs/finance/pricing/)
*   [节省计划](https://dev.qweather.com/docs/finance/savings-plans/)
*   [增值税发票](https://dev.qweather.com/docs/finance/vat-invoice/)

#### [条款](https://dev.qweather.com/docs/terms/)

*   [使用限制](https://dev.qweather.com/docs/terms/restriction/)
*   [注明来源](https://dev.qweather.com/docs/terms/attribution/)
*   [服务条款](https://dev.qweather.com/docs/terms/tos/)

# 特性

和风天气开发服务提供了强大的气象服务，了解我们的服务特性和功能。

[服务和数据](https://dev.qweather.com/docs/features/service-and-data/)
了解和风天气提供的天气、空气质量、天文和海洋等数据服务。

[全球部署](https://dev.qweather.com/docs/features/global-deployment/)
了解和风天气开发服务的多语言、全球城市覆盖和网络加速能力。

[性能](https://dev.qweather.com/docs/features/performance/)
了解和风天气开发服务的服务器性能、请求和连接能力，以及服务可用性保障。

### [和风天气开发者服务](https://dev.qweather.com/)

[联系](https://www.qweather.com/contact/)• [博客](https://blog.qweather.com/)• [GitHub](https://github.com/qwd)

Language 

### 文档

*   [开始使用](https://dev.qweather.com/docs/start/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [开发文档](https://dev.qweather.com/docs/api/)
*   [财务与费用](https://dev.qweather.com/docs/finance/)
*   [最佳实践](https://dev.qweather.com/docs/best-practices/)
*   [实用资料](https://dev.qweather.com/docs/resource/)

### 特性

*   [服务和数据](https://dev.qweather.com/docs/features/service-and-data/)
*   [全球部署](https://dev.qweather.com/docs/features/global-deployment/)
*   [性能](https://dev.qweather.com/docs/features/performance/)

### 更多

*   [天气预报](https://www.qweather.com/)
*   [天气地图](https://map.qweather.com/)
*   [下载APP](https://www.qweather.com/app/)

*   © 2026 [QWeather](https://www.qweather.com/en/)
*   [许可协议](https://www.qweather.com/terms/developers-eula/)
*   [隐私政策](https://www.qweather.com/terms/privacy)
*   [免责声明](https://www.qweather.com/terms/disclaimer/)

[京ICP备15048401号-11](https://beian.miit.gov.cn/)

<!-- END_DOC_65 -->

---


<!-- START_DOC_66: api_time-machine_time-machine-weather.md (URL: https://dev.qweather.com/docs/api/time-machine/time-machine-weather) -->
Title: 天气时光机

URL Source: https://dev.qweather.com/docs/api/time-machine/time-machine-weather

Markdown Content:
[和风天气开发者服务](https://dev.qweather.com/)

[控制台](https://id.qweather.com/#/login?redirect=https://console.qweather.com)

*   [首页](https://dev.qweather.com/)
*   [文档](https://dev.qweather.com/docs/)
*   [图标](https://icons.qweather.com/)
*   [展示](https://dev.qweather.com/showcase/)
*   [帮助](https://dev.qweather.com/help/)
*   [价格](https://dev.qweather.com/price/)
*   Search K 

*   [文档](https://dev.qweather.com/docs/)
*   >
*   [开发文档](https://dev.qweather.com/docs/api/)
*   >
*   [时光机](https://dev.qweather.com/docs/api/time-machine/)
*   >
*   [天气时光机](https://dev.qweather.com/docs/api/time-machine/time-machine-weather/)

#### [开始使用](https://dev.qweather.com/docs/start/)

*   [特性](https://dev.qweather.com/docs/features/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [帐号管理](https://dev.qweather.com/docs/account/)

#### [开发文档](https://dev.qweather.com/docs/api/)

*   [GeoAPI](https://dev.qweather.com/docs/api/geoapi/)
*   [天气预报](https://dev.qweather.com/docs/api/weather/)
*   [分钟预报](https://dev.qweather.com/docs/api/minutely/)
*   [预警](https://dev.qweather.com/docs/api/warning/)
*   [天气指数](https://dev.qweather.com/docs/api/indices/)
*   [空气质量](https://dev.qweather.com/docs/api/air-quality/)
*   [时光机](https://dev.qweather.com/docs/api/time-machine/)

    *   [GET 天气时光机](https://dev.qweather.com/docs/api/time-machine/time-machine-weather/)

*   [热带气旋（台风）](https://dev.qweather.com/docs/api/tropical-cyclone/)
*   [海洋数据](https://dev.qweather.com/docs/api/ocean/)
*   [太阳辐射](https://dev.qweather.com/docs/api/solar-radiation/)
*   [天文](https://dev.qweather.com/docs/api/astronomy/)
*   [控制台API](https://dev.qweather.com/docs/api/console/)

#### [实用资料](https://dev.qweather.com/docs/resource/)

*   [专用词汇表](https://dev.qweather.com/docs/resource/glossary/)
*   [错误码](https://dev.qweather.com/docs/resource/error-code/)
*   [多语言](https://dev.qweather.com/docs/resource/language/)
*   [常见城市列表](https://dev.qweather.com/docs/resource/location-list/)
*   [元数据](https://dev.qweather.com/docs/resource/metadata/)
*   [单位](https://dev.qweather.com/docs/resource/unit/)

#### [最佳实践](https://dev.qweather.com/docs/best-practices/)

*   [不要假设](https://dev.qweather.com/docs/best-practices/no-assumptions/)
*   [优化请求](https://dev.qweather.com/docs/best-practices/optimize-requests/)
*   [处理Gzip](https://dev.qweather.com/docs/best-practices/gzip/)
*   [缓存你的数据](https://dev.qweather.com/docs/best-practices/cache/)
*   [安全指南](https://dev.qweather.com/docs/best-practices/security-guidelines/)

#### [财务与费用](https://dev.qweather.com/docs/finance/)

*   [计费方式和支付](https://dev.qweather.com/docs/finance/billing-and-payment/)
*   [按量计费定价](https://dev.qweather.com/docs/finance/pricing/)
*   [节省计划](https://dev.qweather.com/docs/finance/savings-plans/)
*   [增值税发票](https://dev.qweather.com/docs/finance/vat-invoice/)

#### [条款](https://dev.qweather.com/docs/terms/)

*   [使用限制](https://dev.qweather.com/docs/terms/restriction/)
*   [注明来源](https://dev.qweather.com/docs/terms/attribution/)
*   [服务条款](https://dev.qweather.com/docs/terms/tos/)

# 天气时光机

获取最近10天的天气历史再分析数据。

> 例如今天是12月30日，最多可获取12月20日至12月29日的天气历史数据。

> 和风天气额外提供了2000年至今的历史再分析气象数据，通过数据文件的形式发送，如需要长时间的历史气象数据数据，请提供下列信息，发送邮件至sales@qweather.com，我们将有专人与你联系:
> 
> 
> *   企业名称
> *   联系方式
> *   所需要的城市或坐标
> *   所需要的时间范围

## 请求路径[#](https://dev.qweather.com/docs/api/time-machine/time-machine-weather#endpoint)

`GET /v7/historical/weather`

## 参数[#](https://dev.qweather.com/docs/api/time-machine/time-machine-weather#parameters)

#### 查询参数

*   location 必选 string  需要查询的地区，仅支持[LocationID](https://dev.qweather.com/docs/resource/glossary/#locationid)，LocationID可通过[GeoAPI](https://dev.qweather.com/docs/api/geoapi/)获取。例如 `location=101010100`  
*   date 必选 string  最近10天内的日期，不包含今天，格式为 `yyyyMMdd`。  
*   lang string  [多语言设置](https://dev.qweather.com/docs/resource/language/)  
*   unit string  数据单位，可选 `m`（公制，默认）或 `i`（英制）。  

## 请求示例[#](https://dev.qweather.com/docs/api/time-machine/time-machine-weather#request-example)

```bash
curl -X GET --compressed \
-H 'Authorization: Bearer your_token' \
'https://your-api-host/v7/historical/weather?location=101010100&date=20200725'
```

请将`your_token`替换为你的[JWT身份认证](https://dev.qweather.com/docs/configuration/authentication/)，将`your_api_host`替换为你的[API Host](https://dev.qweather.com/docs/configuration/api-host/)

[试一下](https://dev.qweather.com/api-explore/#/Weather/getHistoricalWeather)

## 返回数据[#](https://dev.qweather.com/docs/api/time-machine/time-machine-weather#response)

```json
{
  "code": "200",
  "fxLink": "https://www.qweather.com",
  "weatherDaily": {
    "date": "2020-07-25",
    "sunrise": "05:08",
    "sunset": "19:33",
    "moonrise": "09:54",
    "moonset": "22:40",
    "moonPhase": "峨眉月",
    "tempMax": "33",
    "tempMin": "23",
    "humidity": "52",
    "precip": "0.0",
    "pressure": "1000"
  },
  "weatherHourly": [
    {
      "time": "2020-07-25 00:00",
      "temp": "28",
      "icon": "100",
      "text": "晴",
      "precip": "0.0",
      "wind360": "246",
      "windDir": "西南风",
      "windScale": "2",
      "windSpeed": "8",
      "humidity": "49",
      "pressure": "1001"
    },
    {
      "time": "2020-07-25 01:00",
      "temp": "27",
      "icon": "101",
      "text": "多云",
      "precip": "0.0",
      "wind360": "350",
      "windDir": "北风",
      "windScale": "1",
      "windSpeed": "4",
      "humidity": "57",
      "pressure": "1001"
    },
    {
      "time": "2020-07-25 02:00",
      "temp": "25",
      "icon": "100",
      "text": "晴",
      "precip": "0.0",
      "wind360": "175",
      "windDir": "南风",
      "windScale": "2",
      "windSpeed": "7",
      "humidity": "63",
      "pressure": "1001"
    },
    {
      "time": "2020-07-25 03:00",
      "temp": "25",
      "icon": "104",
      "text": "阴",
      "precip": "0.0",
      "wind360": "359",
      "windDir": "北风",
      "windScale": "1",
      "windSpeed": "5",
      "humidity": "68",
      "pressure": "1001"
    },
    {
      "time": "2020-07-25 04:00",
      "temp": "23",
      "icon": "100",
      "text": "晴",
      "precip": "0.0",
      "wind360": "126",
      "windDir": "东南风",
      "windScale": "1",
      "windSpeed": "3",
      "humidity": "73",
      "pressure": "1000"
    },
    {
      "time": "2020-07-25 05:00",
      "temp": "23",
      "icon": "100",
      "text": "晴",
      "precip": "0.0",
      "wind360": "166",
      "windDir": "东南风",
      "windScale": "1",
      "windSpeed": "4",
      "humidity": "76",
      "pressure": "1001"
    },
    {
      "time": "2020-07-25 06:00",
      "temp": "23",
      "icon": "100",
      "text": "晴",
      "precip": "0.0",
      "wind360": "69",
      "windDir": "东北风",
      "windScale": "1",
      "windSpeed": "2",
      "humidity": "75",
      "pressure": "1001"
    },
    {
      "time": "2020-07-25 07:00",
      "temp": "26",
      "icon": "104",
      "text": "阴",
      "precip": "0.0",
      "wind360": "358",
      "windDir": "北风",
      "windScale": "1",
      "windSpeed": "4",
      "humidity": "71",
      "pressure": "1001"
    },
    {
      "time": "2020-07-25 08:00",
      "temp": "28",
      "icon": "104",
      "text": "阴",
      "precip": "0.0",
      "wind360": "189",
      "windDir": "南风",
      "windScale": "1",
      "windSpeed": "3",
      "humidity": "57",
      "pressure": "1001"
    },
    {
      "time": "2020-07-25 09:00",
      "temp": "30",
      "icon": "104",
      "text": "阴",
      "precip": "0.0",
      "wind360": "350",
      "windDir": "北风",
      "windScale": "1",
      "windSpeed": "3",
      "humidity": "48",
      "pressure": "1001"
    },
    {
      "time": "2020-07-25 10:00",
      "temp": "32",
      "icon": "104",
      "text": "阴",
      "precip": "0.0",
      "wind360": "180",
      "windDir": "南风",
      "windScale": "1",
      "windSpeed": "2",
      "humidity": "48",
      "pressure": "1001"
    },
    {
      "time": "2020-07-25 11:00",
      "temp": "33",
      "icon": "101",
      "text": "多云",
      "precip": "0.0",
      "wind360": "177",
      "windDir": "南风",
      "windScale": "2",
      "windSpeed": "6",
      "humidity": "40",
      "pressure": "1001"
    },
    {
      "time": "2020-07-25 12:00",
      "temp": "32",
      "icon": "101",
      "text": "多云",
      "precip": "0.0",
      "wind360": "222",
      "windDir": "西南风",
      "windScale": "2",
      "windSpeed": "10",
      "humidity": "39",
      "pressure": "1001"
    },
    {
      "time": "2020-07-25 13:00",
      "temp": "32",
      "icon": "101",
      "text": "多云",
      "precip": "0.0",
      "wind360": "187",
      "windDir": "南风",
      "windScale": "2",
      "windSpeed": "10",
      "humidity": "40",
      "pressure": "1000"
    },
    {
      "time": "2020-07-25 14:00",
      "temp": "33",
      "icon": "101",
      "text": "多云",
      "precip": "0.0",
      "wind360": "266",
      "windDir": "西风",
      "windScale": "2",
      "windSpeed": "9",
      "humidity": "36",
      "pressure": "1000"
    },
    {
      "time": "2020-07-25 15:00",
      "temp": "32",
      "icon": "101",
      "text": "多云",
      "precip": "0.0",
      "wind360": "183",
      "windDir": "南风",
      "windScale": "1",
      "windSpeed": "2",
      "humidity": "39",
      "pressure": "1000"
    },
    {
      "time": "2020-07-25 16:00",
      "temp": "32",
      "icon": "101",
      "text": "多云",
      "precip": "0.0",
      "wind360": "189",
      "windDir": "南风",
      "windScale": "1",
      "windSpeed": "4",
      "humidity": "39",
      "pressure": "999"
    },
    {
      "time": "2020-07-25 17:00",
      "temp": "32",
      "icon": "101",
      "text": "多云",
      "precip": "0.0",
      "wind360": "173",
      "windDir": "南风",
      "windScale": "1",
      "windSpeed": "5",
      "humidity": "42",
      "pressure": "999"
    },
    {
      "time": "2020-07-25 18:00",
      "temp": "32",
      "icon": "101",
      "text": "多云",
      "precip": "0.0",
      "wind360": "173",
      "windDir": "南风",
      "windScale": "2",
      "windSpeed": "9",
      "humidity": "39",
      "pressure": "999"
    },
    {
      "time": "2020-07-25 19:00",
      "temp": "31",
      "icon": "101",
      "text": "多云",
      "precip": "0.0",
      "wind360": "185",
      "windDir": "南风",
      "windScale": "1",
      "windSpeed": "4",
      "humidity": "46",
      "pressure": "999"
    },
    {
      "time": "2020-07-25 20:00",
      "temp": "30",
      "icon": "104",
      "text": "阴",
      "precip": "0.0",
      "wind360": "185",
      "windDir": "南风",
      "windScale": "2",
      "windSpeed": "8",
      "humidity": "44",
      "pressure": "999"
    },
    {
      "time": "2020-07-25 21:00",
      "temp": "29",
      "icon": "104",
      "text": "阴",
      "precip": "0.0",
      "wind360": "208",
      "windDir": "西南风",
      "windScale": "2",
      "windSpeed": "7",
      "humidity": "52",
      "pressure": "1000"
    },
    {
      "time": "2020-07-25 22:00",
      "temp": "29",
      "icon": "104",
      "text": "阴",
      "precip": "0.0",
      "wind360": "188",
      "windDir": "南风",
      "windScale": "2",
      "windSpeed": "7",
      "humidity": "57",
      "pressure": "1000"
    },
    {
      "time": "2020-07-25 23:00",
      "temp": "28",
      "icon": "104",
      "text": "阴",
      "precip": "0.0",
      "wind360": "180",
      "windDir": "南风",
      "windScale": "1",
      "windSpeed": "5",
      "humidity": "61",
      "pressure": "1000"
    }
  ],
  "refer": {
    "sources": [
      "https://developer.qweather.com/attribution.html"
    ],
    "license": [
      "QWeather Developers License"
    ]
  }
}
```

*   code string  [状态码](https://dev.qweather.com/docs/resource/error-code/)  
*   fxLink uri  当前数据的响应式页面，便于嵌入网站或应用  
*   weatherDaily object  历史每日天气数据  
    *   date string  日期  
    *   sunrise string  [日出时间](https://dev.qweather.com/docs/api/astronomy/sun-guide/#sunrise-and-sunset)，在高纬度地区可能为空  
    *   sunset string  [日落时间](https://dev.qweather.com/docs/api/astronomy/sun-guide/#sunrise-and-sunset)，在高纬度地区可能为空  
    *   moonrise string  [月升时间](https://dev.qweather.com/docs/api/astronomy/moon-guide/#moonrise-and-moonset)，可能为空  
    *   moonset string  [月落时间](https://dev.qweather.com/docs/api/astronomy/moon-guide/#moonrise-and-moonset)，可能为空  
    *   moonPhase string  [月相名称](https://dev.qweather.com/docs/api/astronomy/moon-guide/#moon-phase)  
    *   tempMax string  最高温度  
    *   tempMin string  最低温度  
    *   humidity string  相对湿度，百分比数值  
    *   precip string  预报当天总降水量，默认单位：毫米  
    *   pressure string  站点气压，默认单位：百帕  

*   weatherHourly array  历史逐小时天气数据列表  
    *   time string  时间  
    *   temp string  温度，默认单位：摄氏度  
    *   icon string  天气状况的[图标代码](https://dev.qweather.com/docs/api/weather/weather-conditions/#icons)，另请参考[天气图标项目](https://icons.qweather.com/)  
    *   text string  天气状况的文字描述，包括阴晴雨雪等天气状态的描述  
    *   wind360 string  [风向](https://dev.qweather.com/docs/api/weather/wind-guide/#wind-direction)360角度  
    *   windDir string  [风向](https://dev.qweather.com/docs/api/weather/wind-guide/#wind-direction)  
    *   windScale string  [风力等级](https://dev.qweather.com/docs/api/weather/wind-guide/#wind-scale)  
    *   windSpeed string  风速，公里/小时  
    *   humidity string  相对湿度，百分比数值  
    *   precip string  降水量，默认单位：毫米  
    *   pressure string  站点气压，默认单位：百帕  

*   refer object  数据来源和许可信息  
    *   sources array  原始数据来源，或数据源说明，可能为空  

    *   license array  数据许可或版权声明，可能为空  

#### 本页导航

*   [请求路径](https://dev.qweather.com/docs/api/time-machine/time-machine-weather#endpoint)
*   [参数](https://dev.qweather.com/docs/api/time-machine/time-machine-weather#parameters)
*   [请求示例](https://dev.qweather.com/docs/api/time-machine/time-machine-weather#request-example)
*   [返回数据](https://dev.qweather.com/docs/api/time-machine/time-machine-weather#response)

### [和风天气开发者服务](https://dev.qweather.com/)

[联系](https://www.qweather.com/contact/)• [博客](https://blog.qweather.com/)• [GitHub](https://github.com/qwd)

Language 

### 文档

*   [开始使用](https://dev.qweather.com/docs/start/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [开发文档](https://dev.qweather.com/docs/api/)
*   [财务与费用](https://dev.qweather.com/docs/finance/)
*   [最佳实践](https://dev.qweather.com/docs/best-practices/)
*   [实用资料](https://dev.qweather.com/docs/resource/)

### 特性

*   [服务和数据](https://dev.qweather.com/docs/features/service-and-data/)
*   [全球部署](https://dev.qweather.com/docs/features/global-deployment/)
*   [性能](https://dev.qweather.com/docs/features/performance/)

### 更多

*   [天气预报](https://www.qweather.com/)
*   [天气地图](https://map.qweather.com/)
*   [下载APP](https://www.qweather.com/app/)

*   © 2026 [QWeather](https://www.qweather.com/en/)
*   [许可协议](https://www.qweather.com/terms/developers-eula/)
*   [隐私政策](https://www.qweather.com/terms/privacy)
*   [免责声明](https://www.qweather.com/terms/disclaimer/)

[京ICP备15048401号-11](https://beian.miit.gov.cn/)

<!-- END_DOC_66 -->

---


<!-- START_DOC_67: api_weather_weather-conditions.md (URL: https://dev.qweather.com/docs/api/weather/weather-conditions) -->
Title: 天气现象

URL Source: https://dev.qweather.com/docs/api/weather/weather-conditions

Markdown Content:
本文档介绍和风天气 API 中使用的所有天气现象以及对应的代码和图标。

## 图标[#](https://dev.qweather.com/docs/api/weather/weather-conditions#icons)

我们提供了一套开源的天气图标，使用SVG格式，支持图标字体，你可以方便嵌入到网站或APP中。同时，我们还提供了图标的设计源文件，开发者可以自行定义和修改所需要的图标。

通常，在 API 中会使用 `icon` 字段用来对应天气图标，另外也提供了预警和月相的图标可以与 API 中的其他字段关联。

👉 访问 **[和风天气图标](https://icons.qweather.com/)**

## 天气现象[#](https://dev.qweather.com/docs/api/weather/weather-conditions#weather-conditions)

> **注意：** 天气现象和图标将不断的更新，包括新增、修改和删除，因此你必须对你的程序进行适配，以便在发生变化的时候不会导致错误出现。

| `condition.code` | `condition.text` |
| --- | --- |
| 100 | 晴 |
| 101 | 多云 |
| 102 | 少云 |
| 103 | 晴间多云 |
| 104 | 阴 |
| 300 | 阵雨 |
| 301 | 强阵雨 |
| 302 | 雷阵雨 |
| 303 | 强雷阵雨 |
| 304 | 雷阵雨伴有冰雹 |
| 305 | 小雨 |
| 306 | 中雨 |
| 307 | 大雨 |
| 308 | 极端降雨 |
| 309 | 毛毛雨/细雨 |
| 310 | 暴雨 |
| 311 | 大暴雨 |
| 312 | 特大暴雨 |
| 313 | 冻雨 |
| 314 | 小到中雨 |
| 315 | 中到大雨 |
| 316 | 大到暴雨 |
| 317 | 暴雨到大暴雨 |
| 318 | 大暴雨到特大暴雨 |
| 399 | 雨 |
| 400 | 小雪 |
| 401 | 中雪 |
| 402 | 大雪 |
| 403 | 暴雪 |
| 404 | 雨夹雪 |
| 405 | 雨雪天气 |
| 406 | 阵雨夹雪 |
| 407 | 阵雪 |
| 408 | 小到中雪 |
| 409 | 中到大雪 |
| 410 | 大到暴雪 |
| 499 | 雪 |
| 500 | 薄雾 |
| 501 | 雾 |
| 502 | 霾 |
| 503 | 扬沙 |
| 504 | 浮尘 |
| 507 | 沙尘暴 |
| 508 | 强沙尘暴 |
| 509 | 浓雾 |
| 510 | 强浓雾 |
| 511 | 中度霾 |
| 512 | 重度霾 |
| 513 | 严重霾 |
| 514 | 大雾 |
| 515 | 特强浓雾 |
| 900 | 热 |
| 901 | 冷 |
| 999 | 未知 |

_下载 [weather-conditions.csv](https://raw.githubusercontent.com/qwd/dev-site/master/assets/table/weather-conditions.csv)_

<!-- END_DOC_67 -->

---


<!-- START_DOC_68: api_weather.md (URL: https://dev.qweather.com/docs/api/weather) -->
Title: 天气预报

URL Source: https://dev.qweather.com/docs/api/weather

Markdown Content:
## 天气预报

和风天气预报 API 基于多模式融合、站点同化和人工智能算法，支持未来最多30天预报和分钟级实况数据，1公里分辨率，覆盖全球任意地点。

API

[GET 实时天气](https://dev.qweather.com/docs/api/weather/weather-current/)

获取指定经纬度位置的实时天气数据，1公里分辨率，覆盖全球任意地点，分钟级更新。

[GET 每日天气预报](https://dev.qweather.com/docs/api/weather/weather-daily-forecast/)

获取指定经纬度位置未来最多10天的每日天气预报，分辨率为1公里并覆盖全球。

[GET 小时天气预报](https://dev.qweather.com/docs/api/weather/weather-hourly-forecast/)

获取指定经纬度位置未来最多240小时的逐小时天气预报，分辨率为1公里并覆盖全球。

[GET 城市实时天气](https://dev.qweather.com/docs/api/weather/weather-now-webapi-v7/)

获取全球城市的实时天气数据。

[GET 城市每日预报](https://dev.qweather.com/docs/api/weather/weather-daily-forecast-webapi-v7/)

获取全球城市未来3至30天的每日天气预报。

[GET 城市小时预报](https://dev.qweather.com/docs/api/weather/weather-hourly-forecast-webapi-v7/)

获取全球城市未来24至168小时的逐小时天气预报。

参考资料

[天气现象](https://dev.qweather.com/docs/api/weather/weather-conditions/)

本文档介绍和风天气 API 中使用的所有天气现象以及对应的代码和图标。

[风向和等级](https://dev.qweather.com/docs/api/weather/wind-guide/)

了解风向和风力等级（蒲福风级）的关系，以及风向角度、风向16方位和风向8方位的相关知识。

<!-- END_DOC_68 -->

---


<!-- START_DOC_69: api_indices.md (URL: https://dev.qweather.com/docs/api/indices) -->
Title: 天气指数

URL Source: https://dev.qweather.com/docs/api/indices

Markdown Content:
[和风天气开发者服务](https://dev.qweather.com/)

[控制台](https://id.qweather.com/#/login?redirect=https://console.qweather.com)

*   [首页](https://dev.qweather.com/)
*   [文档](https://dev.qweather.com/docs/)
*   [图标](https://icons.qweather.com/)
*   [展示](https://dev.qweather.com/showcase/)
*   [帮助](https://dev.qweather.com/help/)
*   [价格](https://dev.qweather.com/price/)
*   Search K 

*   [文档](https://dev.qweather.com/docs/)
*   >
*   [开发文档](https://dev.qweather.com/docs/api/)
*   >
*   [天气指数](https://dev.qweather.com/docs/api/indices/)

#### [开始使用](https://dev.qweather.com/docs/start/)

*   [特性](https://dev.qweather.com/docs/features/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [帐号管理](https://dev.qweather.com/docs/account/)

#### [开发文档](https://dev.qweather.com/docs/api/)

*   [GeoAPI](https://dev.qweather.com/docs/api/geoapi/)
*   [天气预报](https://dev.qweather.com/docs/api/weather/)
*   [分钟预报](https://dev.qweather.com/docs/api/minutely/)
*   [预警](https://dev.qweather.com/docs/api/warning/)
*   [天气指数](https://dev.qweather.com/docs/api/indices/)

    *   [GET 天气指数预报](https://dev.qweather.com/docs/api/indices/indices-forecast/)
    *   [指数类型](https://dev.qweather.com/docs/api/indices/indices-type/)

*   [空气质量](https://dev.qweather.com/docs/api/air-quality/)
*   [时光机](https://dev.qweather.com/docs/api/time-machine/)
*   [热带气旋（台风）](https://dev.qweather.com/docs/api/tropical-cyclone/)
*   [海洋数据](https://dev.qweather.com/docs/api/ocean/)
*   [太阳辐射](https://dev.qweather.com/docs/api/solar-radiation/)
*   [天文](https://dev.qweather.com/docs/api/astronomy/)
*   [控制台API](https://dev.qweather.com/docs/api/console/)

#### [实用资料](https://dev.qweather.com/docs/resource/)

*   [专用词汇表](https://dev.qweather.com/docs/resource/glossary/)
*   [错误码](https://dev.qweather.com/docs/resource/error-code/)
*   [多语言](https://dev.qweather.com/docs/resource/language/)
*   [常见城市列表](https://dev.qweather.com/docs/resource/location-list/)
*   [元数据](https://dev.qweather.com/docs/resource/metadata/)
*   [单位](https://dev.qweather.com/docs/resource/unit/)

#### [最佳实践](https://dev.qweather.com/docs/best-practices/)

*   [不要假设](https://dev.qweather.com/docs/best-practices/no-assumptions/)
*   [优化请求](https://dev.qweather.com/docs/best-practices/optimize-requests/)
*   [处理Gzip](https://dev.qweather.com/docs/best-practices/gzip/)
*   [缓存你的数据](https://dev.qweather.com/docs/best-practices/cache/)
*   [安全指南](https://dev.qweather.com/docs/best-practices/security-guidelines/)

#### [财务与费用](https://dev.qweather.com/docs/finance/)

*   [计费方式和支付](https://dev.qweather.com/docs/finance/billing-and-payment/)
*   [按量计费定价](https://dev.qweather.com/docs/finance/pricing/)
*   [节省计划](https://dev.qweather.com/docs/finance/savings-plans/)
*   [增值税发票](https://dev.qweather.com/docs/finance/vat-invoice/)

#### [条款](https://dev.qweather.com/docs/terms/)

*   [使用限制](https://dev.qweather.com/docs/terms/restriction/)
*   [注明来源](https://dev.qweather.com/docs/terms/attribution/)
*   [服务条款](https://dev.qweather.com/docs/terms/tos/)

# 天气指数

天气指数是一种根据多种气象要素而计算出的指数，使用不同类型的指数可以直观的了解天气对人类活动的影响。

指数包括洗车指数、穿衣指数、感冒指数、过敏指数、紫外线指数、钓鱼指数等类型。

API

[GET 天气指数预报](https://dev.qweather.com/docs/api/indices/indices-forecast/)
获取中国及全球城市的天气生活指数预报数据。

参考资料

[指数类型](https://dev.qweather.com/docs/api/indices/indices-type/)
了解天气指数支持的国家或地区以及天气指数的类型和等级说明。

### [和风天气开发者服务](https://dev.qweather.com/)

[联系](https://www.qweather.com/contact/)• [博客](https://blog.qweather.com/)• [GitHub](https://github.com/qwd)

Language 

### 文档

*   [开始使用](https://dev.qweather.com/docs/start/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [开发文档](https://dev.qweather.com/docs/api/)
*   [财务与费用](https://dev.qweather.com/docs/finance/)
*   [最佳实践](https://dev.qweather.com/docs/best-practices/)
*   [实用资料](https://dev.qweather.com/docs/resource/)

### 特性

*   [服务和数据](https://dev.qweather.com/docs/features/service-and-data/)
*   [全球部署](https://dev.qweather.com/docs/features/global-deployment/)
*   [性能](https://dev.qweather.com/docs/features/performance/)

### 更多

*   [天气预报](https://www.qweather.com/)
*   [天气地图](https://map.qweather.com/)
*   [下载APP](https://www.qweather.com/app/)

*   © 2026 [QWeather](https://www.qweather.com/en/)
*   [许可协议](https://www.qweather.com/terms/developers-eula/)
*   [隐私政策](https://www.qweather.com/terms/privacy)
*   [免责声明](https://www.qweather.com/terms/disclaimer/)

[京ICP备15048401号-11](https://beian.miit.gov.cn/)

<!-- END_DOC_69 -->

---


<!-- START_DOC_70: api_indices_indices-forecast.md (URL: https://dev.qweather.com/docs/api/indices/indices-forecast) -->
Title: 天气指数预报

URL Source: https://dev.qweather.com/docs/api/indices/indices-forecast

Markdown Content:
## 天气指数预报

获取中国及全球城市的天气生活指数预报数据。 
## 请求路径[#](https://dev.qweather.com/docs/api/indices/indices-forecast#endpoint)

`GET /v7/indices/{days}`

## 参数[#](https://dev.qweather.com/docs/api/indices/indices-forecast#parameters)

#### 路径参数

*   days 必选

string 
预报天数，支持最多3天预报，可选值：`1d`、`3d` 

#### 查询参数

*   type 必选

string 
生活指数的类型ID，包括洗车指数、穿衣指数、钓鱼指数等。可以一次性获取多个类型的生活指数，多个类型用英文`,`分割。例如`type=3,5`。可选值参考[指数类型](https://dev.qweather.com/docs/api/indices/indices-type/)，当选择全部天气指数时，不能再选择其他指数。 
*   location 必选

string 
需要查询地区的[LocationID](https://dev.qweather.com/docs/resource/glossary/#locationid)或以英文逗号分隔的[经度,纬度坐标](https://dev.qweather.com/docs/resource/glossary/#coordinate)（十进制，最多支持小数点后两位），LocationID可通过[GeoAPI](https://dev.qweather.com/docs/api/geoapi/)获取。例如 `location=101010100` 或 `location=116.41,39.92` 
*   lang

string 

## 请求示例[#](https://dev.qweather.com/docs/api/indices/indices-forecast#request-example)

```
curl -X GET --compressed \
-H 'Authorization: Bearer your_token' \
'https://your-api-host/v7/indices/1d?type=1&location=116.41%2C39.92'
```

请将`your_token`替换为你的[JWT身份认证](https://dev.qweather.com/docs/configuration/authentication/)，将`your_api_host`替换为你的[API Host](https://dev.qweather.com/docs/configuration/api-host/)

[试一下](https://dev.qweather.com/api-explore/#/Weather/getWeatherIndices)

## 返回数据[#](https://dev.qweather.com/docs/api/indices/indices-forecast#response)

*   code

string 
*   updateTime

date-time 
*   fxLink

uri 
当前数据的响应式页面，便于嵌入网站或应用 
*   daily

array 
每日预报数据列表 
    *   date

date 
预报日期 
    *   type

string 
生活指数类型ID 
    *   name

string 
生活指数类型的名称 
    *   level

string 
生活指数预报等级 
    *   category

string 
生活指数预报级别名称 
    *   text

string 
生活指数预报的详细描述，可能为空 

*   refer

object 
数据来源和许可信息 
    *   sources

array 
原始数据来源，或数据源说明，可能为空 

    *   license

array 
数据许可或版权声明，可能为空

<!-- END_DOC_70 -->

---


<!-- START_DOC_71: api_astronomy.md (URL: https://dev.qweather.com/docs/api/astronomy) -->
Title: 天文

URL Source: https://dev.qweather.com/docs/api/astronomy

Published Time: Thu, 27 Aug 2026 02:08:36 GMT

Markdown Content:
[和风天气开发者服务](https://dev.qweather.com/)

[控制台](https://id.qweather.com/#/login?redirect=https://console.qweather.com)

*   [首页](https://dev.qweather.com/)
*   [文档](https://dev.qweather.com/docs/)
*   [图标](https://icons.qweather.com/)
*   [展示](https://dev.qweather.com/showcase/)
*   [帮助](https://dev.qweather.com/help/)
*   [价格](https://dev.qweather.com/price/)
*   Search K 

*   [文档](https://dev.qweather.com/docs/)
*   >
*   [开发文档](https://dev.qweather.com/docs/api/)
*   >
*   [天文](https://dev.qweather.com/docs/api/astronomy/)

#### [开始使用](https://dev.qweather.com/docs/start/)

*   [特性](https://dev.qweather.com/docs/features/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [帐号管理](https://dev.qweather.com/docs/account/)

#### [开发文档](https://dev.qweather.com/docs/api/)

*   [GeoAPI](https://dev.qweather.com/docs/api/geoapi/)
*   [天气预报](https://dev.qweather.com/docs/api/weather/)
*   [分钟预报](https://dev.qweather.com/docs/api/minutely/)
*   [预警](https://dev.qweather.com/docs/api/warning/)
*   [天气指数](https://dev.qweather.com/docs/api/indices/)
*   [空气质量](https://dev.qweather.com/docs/api/air-quality/)
*   [时光机](https://dev.qweather.com/docs/api/time-machine/)
*   [热带气旋（台风）](https://dev.qweather.com/docs/api/tropical-cyclone/)
*   [海洋数据](https://dev.qweather.com/docs/api/ocean/)
*   [太阳辐射](https://dev.qweather.com/docs/api/solar-radiation/)
*   [天文](https://dev.qweather.com/docs/api/astronomy/)

    *   [GET 日出日落](https://dev.qweather.com/docs/api/astronomy/sunrise-sunset/)
    *   [GET 月升月落和月相](https://dev.qweather.com/docs/api/astronomy/moon-and-moon-phase/)
    *   [GET 太阳高度角](https://dev.qweather.com/docs/api/astronomy/solar-elevation-angle/)
    *   [了解太阳数据](https://dev.qweather.com/docs/api/astronomy/sun-guide/)
    *   [了解月亮数据](https://dev.qweather.com/docs/api/astronomy/moon-guide/)

*   [控制台API](https://dev.qweather.com/docs/api/console/)

#### [实用资料](https://dev.qweather.com/docs/resource/)

*   [专用词汇表](https://dev.qweather.com/docs/resource/glossary/)
*   [错误码](https://dev.qweather.com/docs/resource/error-code/)
*   [多语言](https://dev.qweather.com/docs/resource/language/)
*   [常见城市列表](https://dev.qweather.com/docs/resource/location-list/)
*   [元数据](https://dev.qweather.com/docs/resource/metadata/)
*   [单位](https://dev.qweather.com/docs/resource/unit/)

#### [最佳实践](https://dev.qweather.com/docs/best-practices/)

*   [不要假设](https://dev.qweather.com/docs/best-practices/no-assumptions/)
*   [优化请求](https://dev.qweather.com/docs/best-practices/optimize-requests/)
*   [处理Gzip](https://dev.qweather.com/docs/best-practices/gzip/)
*   [缓存你的数据](https://dev.qweather.com/docs/best-practices/cache/)
*   [安全指南](https://dev.qweather.com/docs/best-practices/security-guidelines/)

#### [财务与费用](https://dev.qweather.com/docs/finance/)

*   [计费方式和支付](https://dev.qweather.com/docs/finance/billing-and-payment/)
*   [按量计费定价](https://dev.qweather.com/docs/finance/pricing/)
*   [节省计划](https://dev.qweather.com/docs/finance/savings-plans/)
*   [增值税发票](https://dev.qweather.com/docs/finance/vat-invoice/)

#### [条款](https://dev.qweather.com/docs/terms/)

*   [使用限制](https://dev.qweather.com/docs/terms/restriction/)
*   [注明来源](https://dev.qweather.com/docs/terms/attribution/)
*   [服务条款](https://dev.qweather.com/docs/terms/tos/)

# 天文

天文API提供了全球任意地点未来60天的日出日落、太阳高度角、月升月落和月相数据，

API

[GET 日出日落](https://dev.qweather.com/docs/api/astronomy/sunrise-sunset/)
获取全球任意地点未来60天的日出日落时间。

[GET 月升月落和月相](https://dev.qweather.com/docs/api/astronomy/moon-and-moon-phase/)
获取全球城市未来60天的月升月落时间和逐小时月相数据。

[GET 太阳高度角](https://dev.qweather.com/docs/api/astronomy/solar-elevation-angle/)
获取全球任意地点在指定时间的太阳高度角和方位角。

参考资料

[了解太阳数据](https://dev.qweather.com/docs/api/astronomy/sun-guide/)
了解日出日落、晨昏蒙影、太阳正午和太阳子夜等太阳数据的含义及特殊情况。

[了解月亮数据](https://dev.qweather.com/docs/api/astronomy/moon-guide/)
了解月升月落、月亮中天和月相等月亮数据的含义及特殊情况。

### [和风天气开发者服务](https://dev.qweather.com/)

[联系](https://www.qweather.com/contact/)• [博客](https://blog.qweather.com/)• [GitHub](https://github.com/qwd)

Language 

### 文档

*   [开始使用](https://dev.qweather.com/docs/start/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [开发文档](https://dev.qweather.com/docs/api/)
*   [财务与费用](https://dev.qweather.com/docs/finance/)
*   [最佳实践](https://dev.qweather.com/docs/best-practices/)
*   [实用资料](https://dev.qweather.com/docs/resource/)

### 特性

*   [服务和数据](https://dev.qweather.com/docs/features/service-and-data/)
*   [全球部署](https://dev.qweather.com/docs/features/global-deployment/)
*   [性能](https://dev.qweather.com/docs/features/performance/)

### 更多

*   [天气预报](https://www.qweather.com/)
*   [天气地图](https://map.qweather.com/)
*   [下载APP](https://www.qweather.com/app/)

*   © 2026 [QWeather](https://www.qweather.com/en/)
*   [许可协议](https://www.qweather.com/terms/developers-eula/)
*   [隐私政策](https://www.qweather.com/terms/privacy)
*   [免责声明](https://www.qweather.com/terms/disclaimer/)

[京ICP备15048401号-11](https://beian.miit.gov.cn/)

<!-- END_DOC_71 -->

---


<!-- START_DOC_72: terms.md (URL: https://dev.qweather.com/docs/terms) -->
Title: 条款

URL Source: https://dev.qweather.com/docs/terms

Markdown Content:
[和风天气开发者服务](https://dev.qweather.com/)

[控制台](https://id.qweather.com/#/login?redirect=https://console.qweather.com)

*   [首页](https://dev.qweather.com/)
*   [文档](https://dev.qweather.com/docs/)
*   [图标](https://icons.qweather.com/)
*   [展示](https://dev.qweather.com/showcase/)
*   [帮助](https://dev.qweather.com/help/)
*   [价格](https://dev.qweather.com/price/)
*    

*   [文档](https://dev.qweather.com/docs/)
*   >
*   [条款](https://dev.qweather.com/docs/terms/)

#### [开始使用](https://dev.qweather.com/docs/start/)

*   [特性](https://dev.qweather.com/docs/features/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [帐号管理](https://dev.qweather.com/docs/account/)

#### [开发文档](https://dev.qweather.com/docs/api/)

*   [GeoAPI](https://dev.qweather.com/docs/api/geoapi/)
*   [天气预报](https://dev.qweather.com/docs/api/weather/)
*   [分钟预报](https://dev.qweather.com/docs/api/minutely/)
*   [预警](https://dev.qweather.com/docs/api/warning/)
*   [天气指数](https://dev.qweather.com/docs/api/indices/)
*   [空气质量](https://dev.qweather.com/docs/api/air-quality/)
*   [时光机](https://dev.qweather.com/docs/api/time-machine/)
*   [热带气旋（台风）](https://dev.qweather.com/docs/api/tropical-cyclone/)
*   [海洋数据](https://dev.qweather.com/docs/api/ocean/)
*   [太阳辐射](https://dev.qweather.com/docs/api/solar-radiation/)
*   [天文](https://dev.qweather.com/docs/api/astronomy/)
*   [控制台API](https://dev.qweather.com/docs/api/console/)

#### [实用资料](https://dev.qweather.com/docs/resource/)

*   [专用词汇表](https://dev.qweather.com/docs/resource/glossary/)
*   [错误码](https://dev.qweather.com/docs/resource/error-code/)
*   [多语言](https://dev.qweather.com/docs/resource/language/)
*   [常见城市列表](https://dev.qweather.com/docs/resource/location-list/)
*   [元数据](https://dev.qweather.com/docs/resource/metadata/)
*   [单位](https://dev.qweather.com/docs/resource/unit/)

#### [最佳实践](https://dev.qweather.com/docs/best-practices/)

*   [不要假设](https://dev.qweather.com/docs/best-practices/no-assumptions/)
*   [优化请求](https://dev.qweather.com/docs/best-practices/optimize-requests/)
*   [处理Gzip](https://dev.qweather.com/docs/best-practices/gzip/)
*   [缓存你的数据](https://dev.qweather.com/docs/best-practices/cache/)
*   [安全指南](https://dev.qweather.com/docs/best-practices/security-guidelines/)

#### [财务与费用](https://dev.qweather.com/docs/finance/)

*   [计费方式和支付](https://dev.qweather.com/docs/finance/billing-and-payment/)
*   [按量计费定价](https://dev.qweather.com/docs/finance/pricing/)
*   [节省计划](https://dev.qweather.com/docs/finance/savings-plans/)
*   [增值税发票](https://dev.qweather.com/docs/finance/vat-invoice/)

#### [条款](https://dev.qweather.com/docs/terms/)

*   [使用限制](https://dev.qweather.com/docs/terms/restriction/)
*   [注明来源](https://dev.qweather.com/docs/terms/attribution/)
*   [服务条款](https://dev.qweather.com/docs/terms/tos/)

# 条款

了解和风天气服务的授权、版权、限制等条款，使用我们的服务，你同意并遵守这些条款。

[使用限制](https://dev.qweather.com/docs/terms/restriction/)
了解使用和风天气开发服务时常见的合规要求、请求限制、欠款处理和帐号冻结规则。

[注明来源](https://dev.qweather.com/docs/terms/attribution/)
了解在产品中使用和风天气及相关数据时的来源标注要求、展示样式和适用例外。

[服务条款](https://dev.qweather.com/docs/terms/tos/)
查看使用和风天气开发平台需要同意和遵守的开发者许可协议、隐私政策和免责声明。

### [和风天气开发者服务](https://dev.qweather.com/)

[联系](https://www.qweather.com/contact/)• [博客](https://blog.qweather.com/)• [GitHub](https://github.com/qwd)

Language 

### 文档

*   [开始使用](https://dev.qweather.com/docs/start/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [开发文档](https://dev.qweather.com/docs/api/)
*   [财务与费用](https://dev.qweather.com/docs/finance/)
*   [最佳实践](https://dev.qweather.com/docs/best-practices/)
*   [实用资料](https://dev.qweather.com/docs/resource/)

### 特性

*   [服务和数据](https://dev.qweather.com/docs/features/service-and-data/)
*   [全球部署](https://dev.qweather.com/docs/features/global-deployment/)
*   [性能](https://dev.qweather.com/docs/features/performance/)

### 更多

*   [天气预报](https://www.qweather.com/)
*   [天气地图](https://map.qweather.com/)
*   [下载APP](https://www.qweather.com/app/)

*   © 2026 [QWeather](https://www.qweather.com/en/)
*   [许可协议](https://www.qweather.com/terms/developers-eula/)
*   [隐私政策](https://www.qweather.com/terms/privacy)
*   [免责声明](https://www.qweather.com/terms/disclaimer/)

[京ICP备15048401号-11](https://beian.miit.gov.cn/)

<!-- END_DOC_72 -->

---


<!-- START_DOC_73: account_recipients.md (URL: https://dev.qweather.com/docs/account/recipients) -->
Title: 通知接收人

URL Source: https://dev.qweather.com/docs/account/recipients

Markdown Content:
## 通知接收人

你可以为你的帐号添加最多5位通知接收人，这些接收人可以收到你帐号的所有通知。例如，你希望在企业内其他人也能收到账单或服务通知，这时你可以将他们添加到你的通知接收人中。

> **提示：** 所有涉及到帐号安全的通知不会发送给通知接收人，例如身份验证、找回密码等。

### 添加通知接收人[#](https://dev.qweather.com/docs/account/recipients#add-recipients)

1.   [前往控制台设置](https://console.qweather.com/setting)
2.   向下滑动至通知接受人区域
3.   填写电子邮件，点击“发送邀请”

被邀请人将收到一封邀请邮件，需要通过点击邮件中的激活链接表示同意邀请。当接收人同意邀请后，他就可以收到你的帐号内所设置的所有通知。

如果被邀请人没有点击激活链接，或未收到邀请邮件，你可以稍等60分钟后重新发送。请注意，邀请邮件的有效期为24小时，如果超过24小时，邀请链接将失效。如果收到多封邀请邮件，以最新的一封邮件为准。

### 删除通知接收人[#](https://dev.qweather.com/docs/account/recipients#remove-recipients)

你可以在需要删除的电子邮箱旁点击“删除”按钮即可。通知接收人目前无法将自己移除，只能联系你进行删除操作。

<!-- END_DOC_73 -->

---


<!-- START_DOC_74: index.md (URL: https://dev.qweather.com/docs) -->
Title: 文档

URL Source: https://dev.qweather.com/docs

Markdown Content:
## 文档

查看和风天气最新的开发文档，了解如何快速的使用API或SDK获取天气服务。

## API 文档

## [GeoAPI](https://dev.qweather.com/docs/api/geoapi/)

和风天气 GeoAPI 提供全球地理信息、城市搜索、坐标反查等功能。

## [天气预报](https://dev.qweather.com/docs/api/weather/)

和风天气预报 API 基于多模式融合、站点同化和人工智能算法，支持未来最多30天预报和分钟级实况数据，1公里分辨率，覆盖全球任意地点。

## [分钟预报](https://dev.qweather.com/docs/api/minutely/)

分钟级降水API（临近预报）支持中国1公里精度的分钟级降雨预报数据，为每一分钟的降雨进行精准预测。

## [预警](https://dev.qweather.com/docs/api/warning/)

和风天气预警 API 提供了全球官方发布的极端天气预警服务，覆盖全球大部分国家或地区。

## [天气指数](https://dev.qweather.com/docs/api/indices/)

天气生活指数是一种根据多种气象要素而计算出的指数，使用不同类型的指数可以直观的了解天气对人类活动的影响。

## [空气质量](https://dev.qweather.com/docs/api/air-quality/)

空气质量 API 提供指定位置的实时空气质量和预报数据、污染物和健康建议。支持100多个国家或地区的空气质量标准，1公里分辨率。

## [热带气旋（台风）](https://dev.qweather.com/docs/api/tropical-cyclone/)

热带气旋 API 提供中国地区的台风信息，包括台风实时位置、路径和预报数据。

## [海洋数据](https://dev.qweather.com/docs/api/ocean/)

海洋数据 API 提供全球主要港口和城市的潮汐数据。

## [太阳辐射](https://dev.qweather.com/docs/api/solar-radiation/)

太阳辐射 API 提供全球辐射数据，包括DNI、DHI、GHI以及相关气象数据，15分钟间隔，1公里分辨率。

## [天文](https://dev.qweather.com/docs/api/astronomy/)

天文API提供了全球任意地点未来60天的日出日落、太阳高度角、月升月落和月相数据，

## [控制台API](https://dev.qweather.com/docs/api/console/)

控制台 API 提供近实时的财务和请求量数据，你可以根据这些数据评估使用率或者建立财务预警。

<!-- END_DOC_74 -->

---


<!-- START_DOC_75: api_air-quality_pollutant-list.md (URL: https://dev.qweather.com/docs/api/air-quality/pollutant-list) -->
Title: 污染物列表

URL Source: https://dev.qweather.com/docs/api/air-quality/pollutant-list

Published Time: Thu, 27 Aug 2026 02:08:36 GMT

Markdown Content:
空气质量由空气污染物确定，污染物浓度越高，对人体的危害越大。污染物包括固体颗粒、滴液和气体的混合物，它们有多种来源，例如家庭燃料燃烧、工业生产、交通废气、发电、露天焚烧、沙尘等。[世卫组织全球空气质量指南](https://www.who.int/news-room/feature-stories/detail/what-are-the-who-air-quality-guidelines)提到的污染物有PM2.5、PM10、O3、NO2、SO2和CO，然而各国和地区的环境部门对于污染物有不同的定义，例如中国空气质量要求计算6种污染物，欧盟则要求计算5种污染物。

在实践中，AQI 中的污染物并不一致，在一些地区也可能无法提供污染物的详细数据，这是因为：

*   当地规范对污染物的标准不同
*   监测站故障或被关闭
*   监测站不支持某些污染物的监控
*   法律法规的要求

## 首要污染物[#](https://dev.qweather.com/docs/api/air-quality/pollutant-list#primary-pollutant)

浓度值最高或污染物分指数最差的污染物是首要污染物，代表导致当前空气污染的主要成分。

## 污染物分指数[#](https://dev.qweather.com/docs/api/air-quality/pollutant-list#pollutant-sub-index)

污染物分指数是各项污染物的空气质量指数，以便于我们了解和对比当前空气质量各项污染物的等级，其中最差的污染物分指数用于确定首要污染物。

一般来说，最差的分指数即为当前的 AQI 数值，例如：

```
AQI = max {SUB-INDEX1,SUB-INDEX2,SUB-INDEX3,...SUB-INDEXn}
```

## 支持的污染物[#](https://dev.qweather.com/docs/api/air-quality/pollutant-list#supported-pollutants)

请参考下方表格了解目前我们支持的污染物和其单位。

| Code | 名称 | 全称 | 单位 |
| --- | --- | --- | --- |
| `pm10` | PM 10 | 颗粒物（粒径小于等于10µm） | * μg/m³ |
| `pm2p5` | PM 2.5 | 颗粒物（粒径小于等于2.5µm） | * μg/m³ |
| `co` | CO | 一氧化碳 | * mg/m³ * μg/m³ * ppm |
| `no` | NO | 一氧化氮 | * ppm |
| `no2` | NO2 | 二氧化氮 | * μg/m³ * ppb * ppm |
| `so2` | SO2 | 二氧化硫 | * μg/m³ * ppb * ppm |
| `o3` | O3 | 臭氧 | * μg/m³ * ppb * ppm |
| `nmhc` | NMHC | 非甲烷总烃 | * ppmC |

<!-- END_DOC_75 -->

---


<!-- START_DOC_76: account_inactive.md (URL: https://dev.qweather.com/docs/account/inactive) -->
Title: 闲置帐号

URL Source: https://dev.qweather.com/docs/account/inactive

Markdown Content:
## 闲置帐号

为了保护所有用户的安全和隐私信息，和风天气始终致力于更加严格的安全机制和隐私保护措施，这其中包括和风天气将尽可能的缩减对用户个人信息存储时长。当用户不再使用和风天气的服务时，我们依然有对应的安全机制避免用户信息被泄露。

因此，当用户长期未使用和风天气的产品时，我们将该用户帐号标记为“**闲置帐号**”，所有**闲置帐号**将被从和风天气开发服务删除。

## 什么是闲置[#](https://dev.qweather.com/docs/account/inactive#what-is-inactive)

**闲置**指的是用户连续12个月：

*   未登录过[开发控制台](https://console.qweather.com/)
*   且未使用任何开发服务。

## 闲置的后续情况[#](https://dev.qweather.com/docs/account/inactive#what-happens-when-your-account-is-inactive)

当用户满足上述闲置条件后，该帐号将被标记为**闲置帐号**，同时我们会发送电子邮件通知。你可以随时登录控制台以取消“限制帐号”状态，否则在被标记为“限制帐号”的30天之后，我们将关闭开发服务并[删除这个帐号](https://dev.qweather.com/docs/account/management/#delete-account)。

用户可以重新注册和风天气开发服务，但之前被删除的任何信息都将无法恢复。

## 例外[#](https://dev.qweather.com/docs/account/inactive#exception)

如果用户的帐号符合下列一项或多项条件，即使用户的帐号满足了上述闲置的条件，也不会被视为闲置：

*   帐号可用额度大于0元
*   签署了长期协议并且在协议期内的帐号
*   待支付/欠款账单
*   冻结的帐号

<!-- END_DOC_76 -->

---


<!-- START_DOC_77: configuration_project-and-key.md (URL: https://dev.qweather.com/docs/configuration/project-and-key) -->
Title: 项目和凭据

URL Source: https://dev.qweather.com/docs/configuration/project-and-key

Markdown Content:
开始请求API之前，你需要先创建项目和凭据。

## 项目[#](https://dev.qweather.com/docs/configuration/project-and-key#project)

**项目**是承载和风天气数据服务的容器，也可以理解为是你需要开发的一个产品，例如“旅游APP”或者“毕业设计”。项目不仅可以区分你的产品，还可以用于数据统计或费用分摊。

### 创建项目[#](https://dev.qweather.com/docs/configuration/project-and-key#create-project)

你可以最多创建10个项目。

1.   [前往控制台-项目管理](https://console.qweather.com/project)
2.   点击右上角“创建项目”按钮
3.   填写项目名称，项目名称最多20个字符。你可以稍后对名称进行修改。
4.   点击“保存”按钮。

### 删除项目[#](https://dev.qweather.com/docs/configuration/project-and-key#delete-project)

> **警告：** 删除动作不可撤销，删除成功后，该项目及项目中的所有凭据都将被删除且无法恢复，你无法再使用这个项目中的凭据获取数据。

1.   在项目列表中点击你希望删除的项目
2.   在页面最下方点击“删除”按钮
3.   输入项目的名称，点击“确认删除”
4.   你将收到一封身份验证邮件，输入验证码
5.   完成删除

## 凭据[#](https://dev.qweather.com/docs/configuration/project-and-key#credential)

**凭据**用于API的身份认证，是你的核心敏感数据。关于安全的请求API，请参考[安全指南](https://dev.qweather.com/docs/best-practices/security-guidelines/)。关于身份认证的方式，请参考[身份认证](https://dev.qweather.com/docs/configuration/authentication/)。

根据身份认证方式的不同，凭据分为JWT凭据和API KEY凭据。每个项目最多创建20个凭据。

### 添加JWT凭据[#](https://dev.qweather.com/docs/configuration/project-and-key#add-credential-for-jwt)

添加JWT凭据，你需要先创建Ed25519公钥和私钥，参考[身份认证-生成Ed25519](https://dev.qweather.com/docs/configuration/authentication/#generate-ed25519-key)

1.   [前往控制台-项目管理](https://console.qweather.com/project)
2.   在项目列表中点击你需要添加凭据的项目
3.   点击凭据区域右侧的“添加凭据”按钮
4.   输入凭据名称
5.   选择身份认证方式JSON Web Token
6.   填写凭据名称
7.   使用任意文本编辑器打开你的Ed25519的公钥，复制其中的全部内容，这些内容看起来像是：

```
-----BEGIN PUBLIC KEY-----
   MCowBQYDK2VwAyEAARbeZ5AhklFG4gg1Gx5g5bWxMMdsUd6b2MC4wV0/M9Q=
   -----END PUBLIC KEY-----
```

1.   在公钥文本框中粘贴公钥内容
2.   点击“保存”按钮

你将在最后看到创建凭据成功的页面，并且显示了这个凭据的创建日期、ID和SHA256值。出于安全考虑，控制台不会再次显示这个公钥。但你可以使用公钥的SHA256值与本地SHA256进行对比，以便确认使用的是正确的公钥。

### 添加API KEY凭据[#](https://dev.qweather.com/docs/configuration/project-and-key#add-credential-for-api-key)

API KEY提供了简单但安全性较弱的认证方式。我们推荐使用[JWT身份认证](https://dev.qweather.com/docs/configuration/authentication/#json-web-token)。

> **注意：** 从2027年2月1日起，我们将逐步限制使用API KEY进行身份认证的每日请求数量。

> **注意：** SDK v5.x及以上版本仅支持JWT认证。

1.   [前往控制台-项目管理](https://console.qweather.com/project)
2.   在项目列表中点击你需要添加凭据的项目
3.   点击凭据区域右侧的“添加凭据”按钮
4.   输入凭据名称
5.   选择身份认证方式API KEY。
6.   点击“保存”按钮

### 安全限制[#](https://dev.qweather.com/docs/configuration/project-and-key#security-restrictions)

为提高凭据安全性，我们建议为凭据添加安全限制。

1.   [前往控制台-项目管理](https://console.qweather.com/project)
2.   选择你希望添加安全限制的凭据
3.   滑动至应用限制区域，参考[应用限制](https://dev.qweather.com/docs/best-practices/security-guidelines/#app-restrictions)了解详情和规则。
4.   继续向下滑动至API限制区域，参考[API限制](https://dev.qweather.com/docs/best-practices/security-guidelines/#api-restrictions)了解详情和规则。

### 删除凭据[#](https://dev.qweather.com/docs/configuration/project-and-key#delete-credential)

> **警告：** 删除动作不可撤销，删除成功后，你无法再使用这个凭据进行API身份认证。

1.   选择你希望删除的凭据
2.   在网页最下方点击“删除凭据”按钮
3.   输入凭据的名称，点击“确认删除”
4.   你将收到一封身份验证邮件，输入验证码
5.   完成删除

<!-- END_DOC_77 -->

---


<!-- START_DOC_78: api_weather_weather-hourly-forecast.md (URL: https://dev.qweather.com/docs/api/weather/weather-hourly-forecast) -->
Title: 小时天气预报

URL Source: https://dev.qweather.com/docs/api/weather/weather-hourly-forecast

Published Time: Thu, 27 Aug 2026 02:08:36 GMT

Markdown Content:
## 小时天气预报

获取指定经纬度位置的每小时天气预报，最多240小时，1公里分辨率，覆盖全球任意地点。

每小时预报包括：天气现象、温度、体感温度、相对湿度、风向和风速、阵风风速、降水量和概率、海平面气压、能见度、露点温度、云量、紫外线指数等。

## 请求路径[#](https://dev.qweather.com/docs/api/weather/weather-hourly-forecast#endpoint)

`GET /weather/v1/hourly/{latitude}/{longitude}`

## 参数[#](https://dev.qweather.com/docs/api/weather/weather-hourly-forecast#parameters)

#### 路径参数

*   latitude 必选

number 
所需位置的纬度。十进制，最多支持小数点后两位。例如 `39.92` 
*   longitude 必选

number 
所需位置的经度。十进制，最多支持小数点后两位。例如 `116.41` 

#### 查询参数

*   hours

integer 
预报小时数，支持 `1-240` 小时，默认返回 `24` 小时 
*   localTime

boolean 是否返回查询地点的本地时间

可选值： `false` UTC时间（默认），`true` 本地时间  
*   lang

string 

## 请求示例[#](https://dev.qweather.com/docs/api/weather/weather-hourly-forecast#request-example)

```
curl -X GET --compressed \
-H 'Authorization: Bearer your_token' \
'https://your-api-host/weather/v1/hourly/39.92/116.41'
```

请将`your_token`替换为你的[JWT身份认证](https://dev.qweather.com/docs/configuration/authentication/)，将`your_api_host`替换为你的[API Host](https://dev.qweather.com/docs/configuration/api-host/)

[试一下](https://dev.qweather.com/api-explore/#/Weather/getWeatherHourly)

## 返回数据[#](https://dev.qweather.com/docs/api/weather/weather-hourly-forecast#response)

*   metadata

object 
    *   tag

string 
数据唯一标识 
    *   attributions

array 
数据归因信息或声明，必须与当前数据共同显示 

*   hours

array 
逐小时天气预报列表 
    *   forecastTime

date-time 
预报时间 
    *   condition

object 
        *   text

string 
天气现象的本地化描述 
        *   code

string 
天气现象代码 

    *   temperature

object 
温度数据 
        *   value

number 
数值 
        *   unit

string 

    *   feelsLike

object 
体感温度 
        *   value

number 
数值 
        *   unit

string 

    *   humidity

number 
相对湿度，取值范围 `[0, 1]` 
    *   wind

object 
风的数据 
        *   direction

object 
            *   degree

number 
风向角度，取值范围 `[0, 359]` 
            *   compass

string 风向的方位代码

可选值: `n`, `nne`, `ne`, `ene`, `e`, `ese`, `se`, `sse`, `s`, `ssw`, `sw`, `wsw`, `w`, `wnw`, `nw`, `nnw`, `none`, `vrb`  

        *   speed

object 
风速 
            *   value

number 
数值 
            *   unit

string 

        *   scale

number 

    *   windGust

object 
阵风风速 
        *   value

number 
数值 
        *   unit

string 

    *   precipitation

object 
降水数据 
        *   amount

object 
当前数据时段内的累计降水量 
            *   value

number 
数值 
            *   unit

string 

        *   intensity

object 
降水强度 
            *   value

number 
数值 
            *   unit

string 

        *   type

string 
降水类型代码： `rain`（雨）、`snow`（雪）、`ice`（冰粒或冻雨）、 `mixed`（混合降水）、`none`（无降水）、`unknown`（未知） 
        *   probability

number 
预报降水数据，取值范围 `[0, 1]` 

    *   pressure

object 
海平面气压 
        *   value

number 
数值 
        *   unit

string 

    *   visibility

object 
能见度 
        *   value

number 
数值 
        *   unit

string 

    *   dewPoint

object 
露点温度 
        *   value

number 
数值 
        *   unit

string 

    *   cloudCover

number 
云量，取值范围 `[0, 1]` 
    *   uvIndex

number 
紫外线指数，取值范围 `[0, 15]`

<!-- END_DOC_78 -->

---


<!-- START_DOC_79: features_performance.md (URL: https://dev.qweather.com/docs/features/performance) -->
Title: 性能

URL Source: https://dev.qweather.com/docs/features/performance

Markdown Content:
## 性能

和风天气开发服务提供了稳定和高性能的服务保障，这使得你的开发没有后顾之忧。

## 性能[#](https://dev.qweather.com/docs/features/performance#performance)

和风天气开发服务可提供企业级性能的服务：

| **服务** | **按量计费订阅** | **高级订阅** |
| --- | --- | --- |
| **服务器性能** | 商业共享 | 独立部署 |
| **QPM** | 3000 | 50000起 |
| **最大连接数** | 5000 | 50000起 |
| **网络** | 商业共享 | 独立网络 |
| **自动路由** | ✓ | ✓ |
| **数据节点** | 4个 | 10个 |
| **边缘加速** | × | ✓ |

## SLA[#](https://dev.qweather.com/docs/features/performance#sla)

和风天气开发服务可提供如下可用性保障：

| **项目** | **按量计费订阅** | **高级订阅** |
| --- | --- | --- |
| **月可用性** | 99.5% | 99.95% |
| **响应时间** | 工作日2小时 | 工作日2小时 |
| **故障解决** | 工作日8小时 | 工作日4小时 |

<!-- END_DOC_79 -->

---


<!-- START_DOC_80: deprecated.md (URL: https://dev.qweather.com/docs/deprecated) -->
Title: 已弃用的产品文档

URL Source: https://dev.qweather.com/docs/deprecated

Published Time: Thu, 27 Aug 2026 02:08:36 GMT

Markdown Content:
这里保留了已经弃用但依然提供服务的API/SDK文档。对于已经停止服务的产品，将不再提供开发文档。

### Android SDK v4[#](https://dev.qweather.com/docs/deprecated#android-sdk-v4)

*   最后版本: 4.20
*   停服日期: 2026-09-01
*   升级至: [Android SDK v5](https://github.com/qwd/qweather-android-sdk)
*   [下载文档](https://dl.qweather.com/sites/dev/qweather-sdk-v4-android-doc-zh.md)

### iOS SDK v4[#](https://dev.qweather.com/docs/deprecated#ios-sdk-v4)

*   最后版本: 4.20
*   停服日期: 2026-09-01
*   升级至: [iOS SDK v5](https://github.com/qwd/qweather-ios-sdk)
*   [下载文档](https://dl.qweather.com/sites/dev/qweather-sdk-v4-ios-doc-zh.md)

### WebAPI v7[#](https://dev.qweather.com/docs/deprecated#webapi-v7)

下列 WebAPI v7 的服务已经弃用或停止服务

| API | endpoint | 停服日期 | 公告 |
| --- | --- | --- | --- |
| 空气质量 | `/v7/air/now` `/v7/air/5d` | 2026-06-01 | [查看](https://blog.qweather.com/announce/aqi-webapi-v7-is-about-to-close-in-mid-2026/) |
| 太阳辐照 | `/v7/solar-radiation/{hours}` | 2026-09-01 | [查看](https://blog.qweather.com/announce/solar-radiation-webapi-v7-discontinue/) |
| 天气预警 | `/v7/warning/now` | 2026-10-01 | [查看](https://blog.qweather.com/announce/weather-warning-webapi-v7-discontinue/) |
| 格点天气预报 | `/v7/grid-weather/now` `/v7/grid-weather/{days}` `/v7/grid-weather/{hours}` | 2027-06-01 | [查看](https://blog.qweather.com/announce/weather-forecast-webapi-v7-discontinue/) |
| 城市天气预报 | `/v7/weather/now` `/v7/weather/{days}` `/v7/weather/{hours}` | 2027-06-01 | [查看](https://blog.qweather.com/announce/weather-forecast-webapi-v7-discontinue/) |

[下载 WebAPI v7 文档](https://dl.qweather.com/sites/dev/qweather-webapi-v7-deprecated-zh.md)

<!-- END_DOC_80 -->

---


<!-- START_DOC_81: account_management.md (URL: https://dev.qweather.com/docs/account/management) -->
Title: 用户管理

URL Source: https://dev.qweather.com/docs/account/management

Markdown Content:
## 用户管理

了解如何注册以及管理你的帐号信息。

## 注册[#](https://dev.qweather.com/docs/account/management#register)

你需要使用电子邮箱和手机号码[注册帐号](https://id.qweather.com/register)，为了帐号安全与及时的技术升级，你的用户信息应该是真实有效的，和风天气开发者服务**不支持使用临时/一次性邮箱和手机号码**。如果你的联络信息已过时，请及时更新。

关于密码，请不要选择一个简单的密码或与你的其他帐号相同的密码，我们推荐使用至少18位且包含字母、数字和特殊符号的密码。如果你忘记了密码，可以使用你注册的电子邮箱或手机号码[重置密码](https://id.qweather.com/#/forget/process/start)。

## 用户信息[#](https://dev.qweather.com/docs/account/management#user-information)

#### 开发者ID和API Host

你可以在[控制台设置](https://console.qweather.com/setting)中查看。

#### 名字

1.   [前往控制台设置](https://console.qweather.com/setting)
2.   在最上方编辑你的名字
3.   点击旁边的“保存”

#### 联系信息和密码

如果你需要编辑电子邮箱、电话号码、社交帐号和密码，请访问[用户中心](https://id.qweather.com/)。

#### 发票信息

1.   [前往控制台-发票信息](https://console.qweather.com/finance/vat-invoice/info)
2.   编辑信息
3.   点击“保存”按钮

#### 账单信息

1.   [前往账单联系信息](https://console.qweather.com/finance/contact)
2.   编辑信息
3.   点击“保存”按钮

#### 企业开发者信息

请参考[变更企业信息](https://dev.qweather.com/docs/account/developers/#change-organization-information)。

## 关闭服务[#](https://dev.qweather.com/docs/account/management#close-service)

如果你不再使用开发服务，可以选择关闭服务。如果你还希望删除帐号，请在关闭服务后访问[用户中心](https://id.qweather.com/)。

> **警告：**
> 
> 
> *   关闭服务不可恢复，即便重新开启服务，现有信息也不会恢复。
> *   所有用户数据、开发信息、统计记录将被删除。
> *   为财务和税务合规，交易和增值税发票记录将被保留，但其中的用户数据将被移除且不可恢复。

为了帐号安全，关闭服务需满足一些条件：

1.   [前往控制台设置](https://console.qweather.com/setting)
2.   在页面最下方，点击“关闭服务”按钮。
3.   在关闭服务页面点击“检查”按钮。
4.   为了帐号安全，关闭服务需满足一些条件： 
    *   移除所有项目和凭据：请停止API请求并移除所有项目和凭据，完全移除项目需要我们在多个数据节点进行同步，这一过程可能需要1-3小时。
    *   无待支付账单：如有，请先支付账单。
    *   无应计费用：在账单首页的应计费用应该显示为0，否则在完成移除项目后点击“出账”按钮，我们将为此出具账单。
    *   无可用额度：如果你有正数可用额度，请发送工单申请退款。
    *   无申请中的增值税发票：请等待增值税发票的开具或取消申请。

5.   全部检查通过后，点击“下一步”。
6.   填写验证码已证明是你本人。
7.   在最后确认页面，输入你的开发者ID，点击“我确认关闭服务”。

关闭服务后你将收到邮件通知。

## 删除帐号[#](https://dev.qweather.com/docs/account/management#delete-account)

你需要先关闭开发者服务，之后访问[用户中心](https://id.qweather.com/)删除帐号。

请注意：

*   删除帐号的动作不可恢复
*   删除帐号将彻底删除帐号中所有信息，包括用户信息、开发信息、统计数据等
*   这个帐号无法再用于和风天气的所有产品和服务
*   即使你使用相同的信息重新注册帐号，已经被删除的信息依然无法再获取

在删除帐号的过程中，我们会进行身份验证以确保是你本人。

<!-- END_DOC_81 -->

---


<!-- START_DOC_82: best-practices_optimize-requests.md (URL: https://dev.qweather.com/docs/best-practices/optimize-requests) -->
Title: 优化请求

URL Source: https://dev.qweather.com/docs/best-practices/optimize-requests

Markdown Content:
## 优化请求

和风天气会根据你的请求，通过API或SDK的方式向你提供天气服务，在这一过程中，需要尽量优化你的请求，以便更高效的使用我们的服务，这篇文档将介绍几种常见的优化方式。

为了更有效的使用和风天气开发服务，你需要尽量优化你的请求。这篇文档将介绍几种常见的优化方式。

## 构建合法的URL[#](https://dev.qweather.com/docs/best-practices/optimize-requests#building-a-valid-url)

当你使用API获取数据的时候，会通过URL向我们发起请求，例如：

```
https://abcxyz.qweatherapi.com/path/to/data
```

一般来说，这段请求URL不会出现错误，但是在传递一些特殊的参数和值的时候需要特别注意：

### 特殊字符[#](https://dev.qweather.com/docs/best-practices/optimize-requests#special-characters)

根据 [RFC 3986 URI 标准](https://datatracker.ietf.org/doc/html/rfc3986)，URL 中除英文字母、数字和部分非预留字符（- _ . ~）外，其他字符在**请求参数值** 中都必须进行 URL 编码（URL encoding）或称之为百分号编码（Percent-encoding），以确保请求能被正确解析和传输。

请求参数中**无需编码**的字符：

*   英文字母：`A-Z`, `a-z`
*   数字：`0-9`
*   非预留字符：`-``_``.``~`

请求参数**必须编码**的字符：

*   空格，请编码为`%20`，不建议使用 `+`，例如 `new york` ➡️ `new%20york`
*   中文或其他非 ASCII 字符，例如 `北京` ➡️ `%E5%8C%97%E4%BA%AC`
*   英文逗号`,`作为保留字符通常无需编码，但为了确保兼容所有服务器和客户端的解析行为，强烈建议也将其进行 URL编码，避免歧义：`color=blue,red` ➡️ `color=blue%2Cred`
*   上述无需编码字符列表以外的字符

### 无效的空格[#](https://dev.qweather.com/docs/best-practices/optimize-requests#invalid-whitespace)

请求URL中还需要注意那些无效的空格，这种情况常见于复制粘贴操作。例如，当复制一段 Token 时，可能会在其前后不小心夹带空格。粘贴后应仔细检查并手动删除这些空格，否则可能导致请求 URL 格式错误，或传递的参数不正确，进而影响接口的正常访问。

### 中文符号混用[#](https://dev.qweather.com/docs/best-practices/optimize-requests#chinese-punctuation)

在使用允许的特殊字符时，请注意不要与对应的中文符号混淆。例如，查询参数的起始符应使用英文问号`?`，而不是中文问号`？`。符号混用可能导致请求解析失败或行为异常。

## 安全的请求[#](https://dev.qweather.com/docs/best-practices/optimize-requests#secure-requests)

请不要共享完整的请求URL，这有可能泄露你的敏感信息，使用HTTPS、JWT身份认证等方式发送安全的API请求。关于如果保护API安全，请参考[安全指南](https://dev.qweather.com/docs/best-practices/security-guidelines/)。

## 处理错误[#](https://dev.qweather.com/docs/best-practices/optimize-requests#handle-errors)

当你的请求返回[错误码](https://dev.qweather.com/docs/resource/error-code/)时，你需要暂停请求并妥善的处理这些错误，否则这些错误可能看起来像DDoS攻击，我们的安全策略将冻结你的帐号。

> **例如:** 当你传入了错误的参数或KEY，将返回`400`或`403`，此时你应该暂停这一次的请求，排除故障后再继续。否则当程序不断重试而产生大量失败的请求时，这违反了我们的许可协议且被视为一种攻击，我们将中止你的服务并冻结你的帐号。

### 了解状态码[#](https://dev.qweather.com/docs/best-practices/optimize-requests#understanding-error-codes)

请参考[错误码](https://dev.qweather.com/docs/resource/error-code/)。

### 使用指数退避算法处理错误[#](https://dev.qweather.com/docs/best-practices/optimize-requests#using-exponential-backoff-to-handle-errors)

当出现错误的时候，请停止请求并进行检查，待故障排除后再恢复请求。然而一些错误并非由于请求不符合规范而导致的，例如超过每分钟请求限制，没有足够的余额等等，这时你应该使用[指数退避算法](https://en.wikipedia.org/wiki/Exponential_backoff)优化请求。

例如，当你的请求收到`429`状态码时，代表你超过了每分钟请求次数，此时你应该在下次请求开始前添加x秒的等待期。如果下次请求仍然返回`429`，则将等待期延长一倍，再发送另一次请求，以此类推，继续延长等待期，直至不再返回错误状态码。

简单的公式可以参考：

```
t = b^c
```

其中 `t` 代表下次请求的间隔时间，或称之为等待期，`b`代表基数，`c`是发生错误的次数。如果按照上述的例子来讲，假设 `b = 2`，那么在出现第一次错误时，下一次请求应该等待 `2¹ = 2秒`，再下一次请求应等待 `2² = 4秒`，第三次请求等待 `2³ = 8秒`，如果第四次请求响应正常，则恢复之前的请求频率并重置 `c = 1`。

#### 避免冲突

如果你有大量独立设备发送请求，为了避免这些设备产生了相同的等待期而产生冲突（比如都在等待2秒后重新发送，这并不能恢复QPM），你可以在等待期中设置一个随机数，或称之为插槽，这个随机数的可以是`[0, 2]^c - 1`。在上述例子中，前三次的等待期的随机数分别是：

*   第一次：0, 1
*   第二次：0, 1, 2, 3
*   第三次：0, 1, 2, 3, 4, 5, 6, 7

这样你的多个独立设备每次都有不同的等待期而避免了冲突。

#### 截断

等待期不应是无限的，如果连续出现20次错误，那么此时的等待期就已经长达291个小时，这显然是不现实的。因此你需要为等待期设置一个最大值，当达到这个值时，则不再增加c的取值。我们的建议是`c = 10`。

## 按需请求[#](https://dev.qweather.com/docs/best-practices/optimize-requests#requests-on-need)

仅在需要天气数据的时候再进行请求。

> **例如：** 在APP中，天气内容的位置较为靠下，你可以让程序在用户滑动到天气内容部分再进行请求。

如果已经加载了天气内容，你也可以为这个内容设置缓存时间，或者增加一个刷新按钮，让用户手动去刷新数据。关于设置缓存，请参考[缓存你的数据](https://dev.qweather.com/docs/best-practices/cache/)。

<!-- END_DOC_82 -->

---


<!-- START_DOC_83: api_warning.md (URL: https://dev.qweather.com/docs/api/warning) -->
Title: 预警

URL Source: https://dev.qweather.com/docs/api/warning

Markdown Content:
[和风天气开发者服务](https://dev.qweather.com/)

[控制台](https://id.qweather.com/#/login?redirect=https://console.qweather.com)

*   [首页](https://dev.qweather.com/)
*   [文档](https://dev.qweather.com/docs/)
*   [图标](https://icons.qweather.com/)
*   [展示](https://dev.qweather.com/showcase/)
*   [帮助](https://dev.qweather.com/help/)
*   [价格](https://dev.qweather.com/price/)
*   Search K 

*   [文档](https://dev.qweather.com/docs/)
*   >
*   [开发文档](https://dev.qweather.com/docs/api/)
*   >
*   [预警](https://dev.qweather.com/docs/api/warning/)

#### [开始使用](https://dev.qweather.com/docs/start/)

*   [特性](https://dev.qweather.com/docs/features/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [帐号管理](https://dev.qweather.com/docs/account/)

#### [开发文档](https://dev.qweather.com/docs/api/)

*   [GeoAPI](https://dev.qweather.com/docs/api/geoapi/)
*   [天气预报](https://dev.qweather.com/docs/api/weather/)
*   [分钟预报](https://dev.qweather.com/docs/api/minutely/)
*   [预警](https://dev.qweather.com/docs/api/warning/)

    *   [GET 实时天气预警](https://dev.qweather.com/docs/api/warning/weather-alert/)
    *   [关于预警信息](https://dev.qweather.com/docs/api/warning/alert-guide/)
    *   [预警事件列表](https://dev.qweather.com/docs/api/warning/alert-events/)
    *   [预警的覆盖范围](https://dev.qweather.com/docs/api/warning/alert-coverage/)
    *   [预警的变更](https://dev.qweather.com/docs/api/warning/alert-changes/)

*   [天气指数](https://dev.qweather.com/docs/api/indices/)
*   [空气质量](https://dev.qweather.com/docs/api/air-quality/)
*   [时光机](https://dev.qweather.com/docs/api/time-machine/)
*   [热带气旋（台风）](https://dev.qweather.com/docs/api/tropical-cyclone/)
*   [海洋数据](https://dev.qweather.com/docs/api/ocean/)
*   [太阳辐射](https://dev.qweather.com/docs/api/solar-radiation/)
*   [天文](https://dev.qweather.com/docs/api/astronomy/)
*   [控制台API](https://dev.qweather.com/docs/api/console/)

#### [实用资料](https://dev.qweather.com/docs/resource/)

*   [专用词汇表](https://dev.qweather.com/docs/resource/glossary/)
*   [错误码](https://dev.qweather.com/docs/resource/error-code/)
*   [多语言](https://dev.qweather.com/docs/resource/language/)
*   [常见城市列表](https://dev.qweather.com/docs/resource/location-list/)
*   [元数据](https://dev.qweather.com/docs/resource/metadata/)
*   [单位](https://dev.qweather.com/docs/resource/unit/)

#### [最佳实践](https://dev.qweather.com/docs/best-practices/)

*   [不要假设](https://dev.qweather.com/docs/best-practices/no-assumptions/)
*   [优化请求](https://dev.qweather.com/docs/best-practices/optimize-requests/)
*   [处理Gzip](https://dev.qweather.com/docs/best-practices/gzip/)
*   [缓存你的数据](https://dev.qweather.com/docs/best-practices/cache/)
*   [安全指南](https://dev.qweather.com/docs/best-practices/security-guidelines/)

#### [财务与费用](https://dev.qweather.com/docs/finance/)

*   [计费方式和支付](https://dev.qweather.com/docs/finance/billing-and-payment/)
*   [按量计费定价](https://dev.qweather.com/docs/finance/pricing/)
*   [节省计划](https://dev.qweather.com/docs/finance/savings-plans/)
*   [增值税发票](https://dev.qweather.com/docs/finance/vat-invoice/)

#### [条款](https://dev.qweather.com/docs/terms/)

*   [使用限制](https://dev.qweather.com/docs/terms/restriction/)
*   [注明来源](https://dev.qweather.com/docs/terms/attribution/)
*   [服务条款](https://dev.qweather.com/docs/terms/tos/)

# 预警

和风天气预警 API 提供了全球官方发布的极端天气预警服务，覆盖全球大部分国家或地区，目前已包括数百种灾害预警事件，提供预警详情、严重等级、紧迫程度、确定性、防御指南、颜色、预警条件、生效和过期时间等。

API

[GET 实时天气预警](https://dev.qweather.com/docs/api/warning/weather-alert/)
根据指定经纬度查询全球国家和地区正在生效的官方天气预警信息。

参考资料

[关于预警信息](https://dev.qweather.com/docs/api/warning/alert-guide/)
了解预警信息的时效性和影响区域的约定。

[预警事件列表](https://dev.qweather.com/docs/api/warning/alert-events/)
和风天气预警 API 支持的预警事件列表。

[预警的覆盖范围](https://dev.qweather.com/docs/api/warning/alert-coverage/)
和风天气预警 API 支持的国家和地区列表。

[预警的变更](https://dev.qweather.com/docs/api/warning/alert-changes/)
天气预警不是一个发布后保持不变的静态对象，随着天气系统的发展，预警信息可能随之变更。

### [和风天气开发者服务](https://dev.qweather.com/)

[联系](https://www.qweather.com/contact/)• [博客](https://blog.qweather.com/)• [GitHub](https://github.com/qwd)

Language 

### 文档

*   [开始使用](https://dev.qweather.com/docs/start/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [开发文档](https://dev.qweather.com/docs/api/)
*   [财务与费用](https://dev.qweather.com/docs/finance/)
*   [最佳实践](https://dev.qweather.com/docs/best-practices/)
*   [实用资料](https://dev.qweather.com/docs/resource/)

### 特性

*   [服务和数据](https://dev.qweather.com/docs/features/service-and-data/)
*   [全球部署](https://dev.qweather.com/docs/features/global-deployment/)
*   [性能](https://dev.qweather.com/docs/features/performance/)

### 更多

*   [天气预报](https://www.qweather.com/)
*   [天气地图](https://map.qweather.com/)
*   [下载APP](https://www.qweather.com/app/)

*   © 2026 [QWeather](https://www.qweather.com/en/)
*   [许可协议](https://www.qweather.com/terms/developers-eula/)
*   [隐私政策](https://www.qweather.com/terms/privacy)
*   [免责声明](https://www.qweather.com/terms/disclaimer/)

[京ICP备15048401号-11](https://beian.miit.gov.cn/)

<!-- END_DOC_83 -->

---


<!-- START_DOC_84: api_warning_alert-changes.md (URL: https://dev.qweather.com/docs/api/warning/alert-changes) -->
Title: 预警的变更

URL Source: https://dev.qweather.com/docs/api/warning/alert-changes

Markdown Content:
天气预警不是一个发布后保持不变的静态对象，随着天气过程的发展，预警信息可能经过一次或多次更新。根据 `messageType` （预警信息类型）可以得知当前预警信息是首次发布还是对之前预警的更新。

`messageType` 包括：

*   `alert` 初始预警信息
*   `update` 表示对之前已经发布的预警进行更新
*   `cancel` 表示主动取消之前已经发布的预警

## Alert[#](https://dev.qweather.com/docs/api/warning/alert-changes#alert)

`alert` 表示一条新的预警首次发布。

例如：

```
id:              A
issuedTime:      10:00
messageType:     alert
supersedes:      []
eventType:       wind
severity:        minor
expireTime:      18:00
```

这代表一个新的预警生命周期开始。

## Update[#](https://dev.qweather.com/docs/api/warning/alert-changes#update)

`update` 表示对之前已经发布的预警进行更新。更新可能涉及：预警等级、影响范围、过期时间等。

例如：

```
id:              B
issuedTime:      13:00
messageType:     update
supersedes:      [A]
eventType:       wind
severity:        moderate
expireTime:      20:00
```

这表示当前预警 B 是一条性质为`update`的预警信息，它在 13:00 取代了预警 A，变更的内容包括严重程度升高，过期时间延长。此时预警 API 中不再返回预警 A。

## Cancel[#](https://dev.qweather.com/docs/api/warning/alert-changes#cancel)

`cancel` 表示主动取消此前的预警。

例如：

```
id:              C
issuedTime:      15:00
messageType:     cancel
supersedes:      [B]
eventType:       wind
severity:        moderate
expireTime:      16:00
```

这表示当前预警 C 是一条性质为`cancel`的预警信息，它在 15:00 取代了预警 B，变更内容为取消预警信息。此时预警 API 中不再返回预警 B。另外，预警 C 也有自己的过期时间，默认为发布后的1小时。

<!-- END_DOC_84 -->

---


<!-- START_DOC_85: api_warning_alert-coverage.md (URL: https://dev.qweather.com/docs/api/warning/alert-coverage) -->
Title: 预警的覆盖范围

URL Source: https://dev.qweather.com/docs/api/warning/alert-coverage

Markdown Content:
## 预警的覆盖范围

和风天气预警 API 已经覆盖大部分国家和地区，我们将不断的扩展这些数据，目前和风天气预警服务支持的国家和地区如下：

| ISO 3166-1 | 国家或地区 |
| --- | --- |
| ar | 阿根廷 |
| au | 澳大利亚 |
| at | 奥地利 |
| by | 白俄罗斯 |
| be | 比利时 |
| ba | 波黑 |
| br | 巴西 |
| bg | 保加利亚 |
| ca | 加拿大 |
| cn | 中国 |
| hr | 克罗地亚 |
| cy | 塞浦路斯 |
| cz | 捷克 |
| dk | 丹麦 |
| ee | 爱沙尼亚 |
| fi | 芬兰 |
| fr | 法国 |
| de | 德国 |
| gr | 希腊 |
| hk | 中国香港 |
| hu | 匈牙利 |
| is | 冰岛 |
| in | 印度 |
| id | 印尼 |
| ie | 爱尔兰 |
| il | 以色列 |
| it | 意大利 |
| kz | 哈萨克斯坦 |
| kr | 韩国 |
| kw | 科威特 |
| lv | 拉脱维亚 |
| lt | 立陶宛 |
| lu | 卢森堡 |
| mo | 中国澳门 |
| mt | 马耳他 |
| md | 摩尔多瓦 |
| me | 黑山 |
| nl | 荷兰 |
| nz | 新西兰 |
| mk | 北马其顿 |
| no | 挪威 |
| pl | 波兰 |
| pt | 葡萄牙 |
| ro | 罗马尼亚 |
| ru | 俄罗斯 |
| sa | 沙特阿拉伯 |
| rs | 塞尔维亚 |
| sg | 新加坡 |
| sk | 斯洛伐克 |
| si | 斯洛文尼亚 |
| za | 南非 |
| es | 西班牙 |
| se | 瑞典 |
| ch | 瑞士 |
| tw | 中国台湾省 |
| th | 泰国 |
| ua | 乌克兰 |
| gb | 英国 |
| us | 美国 |

<!-- END_DOC_85 -->

---


<!-- START_DOC_86: api_warning_alert-events.md (URL: https://dev.qweather.com/docs/api/warning/alert-events) -->
Title: 预警事件列表

URL Source: https://dev.qweather.com/docs/api/warning/alert-events

Markdown Content:
和风天气预警 API 支持的预警事件列表，我们为各国/地区的预警事件提供了独立的标识以便区分。

预警事件在各国可能有类似的事件，例如大风预警，因此可能会有重名的预警事件。预警事件可能会变更，开发者应做好适配。

| Code | Event |
| --- | --- |
| 1001 | 台风 |
| 1002 | 龙卷风 |
| 1003 | 暴雨 |
| 1004 | 暴雪 |
| 1005 | 寒潮 |
| 1006 | 大风 |
| 1007 | 沙尘暴 |
| 1008 | 低温冻害 |
| 1009 | 高温 |
| 1010 | 热浪 |
| 1011 | 干热风 |
| 1012 | 下击暴流 |
| 1013 | 雪崩 |
| 1014 | 雷电 |
| 1015 | 冰雹 |
| 1016 | 霜冻 |
| 1017 | 大雾 |
| 1018 | 低空风切变 |
| 1019 | 霾 |
| 1020 | 雷雨大风 |
| 1021 | 道路结冰 |
| 1022 | 干旱 |
| 1023 | 海上大风 |
| 1024 | 高温中暑 |
| 1025 | 森林火险 |
| 1026 | 草原火险 |
| 1027 | 冰冻 |
| 1028 | 空间天气 |
| 1029 | 重污染 |
| 1030 | 低温雨雪冰冻 |
| 1031 | 强对流 |
| 1032 | 臭氧 |
| 1033 | 大雪 |
| 1034 | 寒冷 |
| 1035 | 连阴雨 |
| 1036 | 渍涝风险 |
| 1037 | 地质灾害 |
| 1038 | 强降雨 |
| 1039 | 强降温 |
| 1040 | 雪灾 |
| 1041 | 森林（草原）火险 |
| 1042 | 医疗气象 |
| 1043 | 雷暴 |
| 1044 | 停课信号 |
| 1045 | 停工信号 |
| 1046 | 海上风险 |
| 1047 | 春季沙尘天气 |
| 1048 | 降温 |
| 1049 | 台风暴雨 |
| 1050 | 严寒 |
| 1051 | 沙尘 |
| 1052 | 海上雷雨大风 |
| 1053 | 海上大雾 |
| 1054 | 海上雷电 |
| 1055 | 海上台风 |
| 1056 | 低温 |
| 1057 | 道路冰雪 |
| 1058 | 雷暴大风 |
| 1059 | 持续低温 |
| 1060 | 能见度不良 |
| 1061 | 浓浮沉 |
| 1062 | 海区大风 |
| 1063 | 短历时强降水 |
| 1064 | 短时强降雨 |
| 1065 | 海区大雾 |
| 1066 | 中暑气象条件 |
| 1067 | 重污染天气 |
| 1068 | 一氧化碳中毒气象条件 |
| 1069 | 感冒等呼吸道疾病气象条件 |
| 1071 | 腹泻等肠道疾病气象条件 |
| 1072 | 心脑血管疾病气象条件 |
| 1073 | 洪涝灾害气象风险 |
| 1074 | 重污染气象条件 |
| 1075 | 城市内涝气象风险 |
| 1076 | 洪水灾害气象风险 |
| 1077 | 森林火险气象风险 |
| 1078 | 气象干旱 |
| 1079 | 农业气象风险 |
| 1080 | 强季风 |
| 1081 | 电线积冰 |
| 1082 | 脑卒中气象风险 |
| 1084 | 森林（草原）火灾气象风险 |
| 1085 | 雷雨强风 |
| 1086 | 低温凝冻 |
| 1087 | 低温冷害 |
| 1088 | 全国农业气象灾害风险 |
| 1089 | 冬小麦干热风灾害风险预警 |
| 1201 | 洪水 |
| 1202 | 内涝 |
| 1203 | 水库重大险情 |
| 1204 | 堤防重大险情 |
| 1205 | 凌汛灾害 |
| 1206 | 渍涝 |
| 1207 | 洪涝 |
| 1208 | 枯水 |
| 1209 | 中小河流洪水和山洪气象风险 |
| 1210 | 农村人畜饮水困难 |
| 1211 | 中小河流洪水气象风险 |
| 1212 | 防汛抗旱风险提示 |
| 1213 | 城市内涝风险 |
| 1214 | 山洪灾害事件 |
| 1215 | 农业干旱 |
| 1216 | 城镇缺水 |
| 1217 | 生态干旱 |
| 1218 | 灾害风险预警 |
| 1219 | 山洪灾害气象风险 |
| 1221 | 水利旱情 |
| 1241 | 滑坡事件 |
| 1242 | 泥石流事件 |
| 1243 | 山体崩塌事件 |
| 1244 | 地面塌陷事件 |
| 1245 | 地裂缝事件 |
| 1246 | 地面沉降事件 |
| 1247 | 火山喷发事件 |
| 1248 | 地质灾害气象风险 |
| 1249 | 地质灾害气象 |
| 1250 | 地质灾害 |
| 1251 | 地质灾害风险 |
| 1271 | 空气污染事件 |
| 1272 | 空气重污染 |
| 1273 | 大气污染 |
| 1274 | 重污染天气 |
| 1601 | 炎热 |
| 1602 | 强烈季风信号 |
| 1603 | 山泥倾泻 |
| 1604 | 热带气旋 |
| 1605 | 火灾危险 |
| 1606 | 新界北部水浸特别报告 |
| 1607 | 寒冷天气 |
| 1608 | 雷暴 |
| 1609 | 暴雨 |
| 1610 | 霜冻 |
| 1701 | 低温 |
| 1702 | 强风 |
| 1703 | 降雨 |
| 1704 | 台风 |
| 1705 | 浓雾 |
| 1706 | 雷雨 |
| 1707 | 高温 |
| 1708 | 淹水 |
| 1709 | 河川高水位 |
| 1710 | 枯旱 |
| 1801 | 强烈季风信号（黑球） |
| 1802 | 风暴潮 |
| 1803 | 热带气旋 |
| 1804 | 暴雨 |
| 1805 | 雷暴 |
| 2001 | 大风 |
| 2002 | 强降雪和结冰 |
| 2003 | 大雾 |
| 2004 | 海岸风险 |
| 2005 | 森林火险 |
| 2006 | 雨 |
| 2007 | 大雨洪水 |
| 2029 | 雷暴 |
| 2030 | 高温 |
| 2031 | 低温 |
| 2032 | 雪崩 |
| 2033 | 洪水 |
| 2050 | 大雨 |
| 2051 | 大风 |
| 2052 | 大雪 |
| 2053 | Zonda wind |
| 2054 | 暴风 |
| 2070 | 扬尘风 |
| 2071 | 强地面风 |
| 2072 | 炎热 |
| 2073 | 夜间炎热 |
| 2074 | 寒冷 |
| 2075 | 雷暴和闪电 |
| 2076 | 冰雹风暴 |
| 2077 | 海况风险 |
| 2078 | 渔业风险 |
| 2079 | 大雪 |
| 2080 | 沙尘暴 |
| 2081 | 热浪 |
| 2082 | 寒潮 |
| 2083 | 大雾 |
| 2084 | 强降雨 |
| 2085 | 地面霜 |
| 2100 | 大雾 |
| 2101 | 雷雨 |
| 2102 | 雷暴 |
| 2103 | 小雨 |
| 2104 | 大雨 |
| 2105 | 风 |
| 2106 | 雷暴和沙尘 |
| 2107 | 沙尘 |
| 2108 | 高海浪 |
| 2109 | 霜 |
| 2111 | 能见度降低 |
| 2120 | 低湿度 |
| 2121 | 累计降水风险 |
| 2122 | 寒潮 |
| 2123 | 龙卷风 |
| 2124 | 雷暴 |
| 2125 | 冰雹 |
| 2126 | 强降雨 |
| 2127 | 大风 |
| 2128 | 热浪 |
| 2129 | 寒冷 |
| 2130 | 霜冻 |
| 2131 | 干旱 |
| 2132 | 森林火险 |
| 2133 | 大雪 |
| 2134 | 强降温 |
| 2135 | 暴雨 |
| 2150 | 大风 |
| 2152 | 冰冻 |
| 2153 | 雷暴 |
| 2154 | 大雾 |
| 2155 | 高温 |
| 2156 | 低温 |
| 2157 | 海岸风险 |
| 2158 | 森林火险 |
| 2159 | 雪崩 |
| 2160 | 雨 |
| 2161 | 洪水 |
| 2162 | 大雨洪水 |
| 2163 | 泥石流 |
| 2164 | 沙尘暴 |
| 2165 | 冻雨和结冰 |
| 2166 | 其他 |
| 2167 | 低水位 |
| 2168 | 大雪 |
| 2190 | 雷暴 |
| 2191 | 破坏性大风 |
| 2192 | 草原火灾条件 |
| 2193 | 天气预警建议 |
| 2200 | 雷雨 |
| 2201 | 飑 |
| 2202 | 空气质量 |
| 2203 | 降雨 |
| 2204 | 大雾 |
| 2205 | 炎热 |
| 2207 | 野火 |
| 2208 | 大风 |
| 2209 | 冻雨 |
| 2210 | 龙卷风 |
| 2211 | 暴风雪 |
| 2212 | 天气预警 |
| 2213 | 冬季风暴 |
| 2214 | 冻毛毛雨 |
| 2215 | 大雪 |
| 2216 | 吹雪 |
| 2217 | 极端寒冷 |
| 2218 | 霜冻 |
| 2219 | 寒冷 |
| 2220 | 北极冷空气外流 |
| 2221 | 雪飑 |
| 2300 | 危险海域警报 |
| 2301 | 重度冷冻喷雾警报 |
| 2302 | 红旗警报 |
| 2303 | 冰冻警报 |
| 2304 | 强冰冻警报 |
| 2305 | 极寒警报 |
| 2306 | 风寒警报 |
| 2307 | 大风警报 |
| 2308 | 过热警报 |
| 2309 | 湖泊效应雪警报 |
| 2311 | 扬尘警报 |
| 2312 | 沙尘暴警报 |
| 2313 | 风暴警报 |
| 2314 | 热带风暴警报 |
| 2315 | 大风警报 |
| 2316 | 高海浪警报 |
| 2317 | 洪水警报 |
| 2318 | 湖岸洪水警报 |
| 2319 | 沿海洪水警报 |
| 2320 | 灰烬警报 |
| 2321 | 火山警报 |
| 2322 | 地震预警 |
| 2323 | 雪崩警报 |
| 2324 | 冬季风暴警报 |
| 2325 | 冰风暴警报 |
| 2326 | 暴风雪警报 |
| 2327 | 暴风雪警报 |
| 2328 | 特别海洋警报 |
| 2330 | 台风警报 |
| 2331 | 飓风警报 |
| 2332 | 飓风警报 |
| 2333 | 风暴潮警报 |
| 2341 | 山洪警报 |
| 2343 | 强雷暴警报 |
| 2345 | 强风警报 |
| 2346 | 龙卷风警报 |
| 2348 | 海啸警报 |
| 2349 | 火灾天气关注 |
| 2350 | 冰冻关注 |
| 2351 | 强冰冻关注 |
| 2352 | 风寒关注 |
| 2353 | 极寒关注 |
| 2354 | 过热关注 |
| 2355 | 大风关注 |
| 2356 | 洪水关注 |
| 2357 | 湖岸洪水关注 |
| 2358 | 沿海洪水关注 |
| 2359 | 重度冷冻喷雾关注 |
| 2360 | 危险海域关注 |
| 2361 | 冬季风暴关注 |
| 2362 | 大风警戒 |
| 2363 | 雪崩关注 |
| 2364 | 风暴关注 |
| 2365 | 热带风暴关注 |
| 2366 | 台风关注 |
| 2367 | 飓风关注 |
| 2368 | 飓风关注 |
| 2369 | 风暴潮关注 |
| 2370 | 山洪关注 |
| 2371 | 强雷暴关注 |
| 2372 | 龙卷风关注 |
| 2373 | 海啸关注 |
| 2374 | 空气不易扩散通知 |
| 2375 | 低水位通知 |
| 2376 | 冷冻喷雾通知 |
| 2377 | 冻雾通知 |
| 2378 | 灰烬通知 |
| 2379 | 霜冻通知 |
| 2380 | 大风通知 |
| 2381 | 湖风通知 |
| 2382 | 扬尘通知 |
| 2383 | 粉尘通知 |
| 2384 | 大风通知 |
| 2385 | 小型船只风险通知 |
| 2386 | 小型船只风速通知 |
| 2387 | 河流入口小型船只通知 |
| 2388 | 危险海域小型船只通知 |
| 2389 | 浓烟通知 |
| 2390 | 浓雾通知 |
| 2391 | 海浪高涨通知 |
| 2392 | 沿海洪水通知 |
| 2393 | 湖岸洪水通知 |
| 2394 | 水文通知 |
| 2395 | 洪水通知 |
| 2396 | 高温通知 |
| 2397 | 风寒通知 |
| 2398 | 冬季天气通知 |
| 2399 | 雪崩通知 |
| 2400 | 海啸通知 |
| 2409 | 洪水报告 |
| 2411 | 水文展望 |
| 2412 | 危险天气展望 |
| 2413 | 空气质量警报 |
| 2414 | 极端火灾危险 |
| 2415 | 海洋天气报告 |
| 2416 | 特殊天气报告 |
| 2417 | 湖岸洪水报告 |
| 2418 | 沿海洪水报告 |
| 2419 | 海滩危害报告 |
| 2420 | 激流报告 |
| 2421 | 热带低气压当地报告 |
| 2422 | 热带风暴当地报告 |
| 2423 | 当地台风报告 |
| 2424 | 当地飓风报告 |
| 2425 | 恶劣天气报告 |
| 2426 | 山洪暴发报告 |
| 2501 | 大雨 |
| 2502 | 暴雨 |
| 2521 | 中雨 |
| 2522 | 雨 |
| 2523 | 大雨 |
| 2524 | 雾 |
| 2525 | 扬尘 |
| 2526 | 大风 |
| 2527 | 浮尘 |
| 2528 | 活跃强风 |
| 2529 | 热浪 |
| 2530 | 寒潮 |
| 2531 | 雾 |
| 2532 | 薄雾 |
| 2550 | 大雨 |
| 2551 | 大风 |
| 2552 | 大雪 |
| 2553 | 雷暴 |
| 2554 | 道路积雪 |
| 2581 | 雷暴 |
| 2601 | 强风提示 |
| 2602 | 强风警报 |
| 2603 | 风浪提示 |
| 2604 | 风浪警报 |
| 2605 | 大雨提示 |
| 2606 | 大雨警报 |
| 2607 | 大雪提示 |
| 2608 | 大雪警报 |
| 2609 | 干燥提示 |
| 2610 | 干燥警报 |
| 2611 | 风暴潮提示 |
| 2612 | 风暴潮警报 |
| 2613 | 寒潮提示 |
| 2614 | 寒潮警报 |
| 2615 | 台风提示 |
| 2616 | 台风警报 |
| 2617 | 黄尘提示 |
| 2618 | 黄尘警报 |
| 2619 | 热浪提示 |
| 2620 | 热浪警报 |
| 2641 | 大雨 |
| 2713 | 牧羊人预警 |
| 2722 | 机场雷电威胁 |
| 2723 | 机场雷暴威胁 |
| 2743 | 火险气象 |
| 2749 | 洪水 |
| 2751 | 山洪暴发 |
| 2752 | 高水位 |
| 2753 | 特大潮汐 |
| 2755 | 风暴潮 |
| 2756 | 河流性洪水 |
| 2791 | 海洋预警 |
| 2792 | 海面结冰喷雾 |
| 2793 | 烈风 |
| 2794 | 飓风级大风 |
| 2795 | 冰山 |
| 2796 | 沿海大浪 |
| 2797 | 巨涌浪 |
| 2801 | 飑 |
| 2802 | 暴风级大风 |
| 2803 | 强风 |
| 2804 | 水龙卷 |
| 2839 | 风暴 |
| 2840 | 暴风雪 |
| 2841 | 沙尘暴 |
| 2842 | 冰雹 |
| 2843 | 强降雨 |
| 2844 | 降雪 |
| 2845 | 雷暴 |
| 2846 | 龙卷风 |
| 2847 | 热带气旋 |
| 2848 | 一般天气预警 |
| 2849 | 气温预警 |
| 2850 | 极端高温 |
| 2851 | 暴露风险 |
| 2852 | 霜冻 |
| 2853 | 风寒效应 |
| 2873 | 大风 |
| 2874 | 风突变 |
| 3101 | 大雨 |
| 3102 | 大风 |
| 3103 | 大雪 |
| 3104 | 大雾 |
| 3105 | 冻雨和结冰 |
| 3106 | 其他预警 |
| 3107 | 低温 |
| 3131 | 风 |
| 3132 | 雪 / 结冰 |
| 3133 | 霜冻 |
| 3134 | 雷暴 |
| 3135 | 雾 |
| 3136 | 高温 |
| 3137 | 低温 |
| 3138 | 沿海活动 |
| 3139 | 火灾 |
| 3140 | 雪崩 |
| 3141 | 雨 |
| 3142 | 洪水 |
| 3143 | 低水位 |
| 3144 | 泥石流 |
| 3145 | 沙尘暴 |
| 3146 | 冻雨和结冰 |
| 3147 | 其他危险 |
| 3148 | 洪水 |
| 9999 | 其他预警 |

<!-- END_DOC_86 -->

---


<!-- START_DOC_87: resource_metadata.md (URL: https://dev.qweather.com/docs/resource/metadata) -->
Title: 元数据

URL Source: https://dev.qweather.com/docs/resource/metadata

Markdown Content:
[和风天气开发者服务](https://dev.qweather.com/)

[控制台](https://id.qweather.com/#/login?redirect=https://console.qweather.com)

*   [首页](https://dev.qweather.com/)
*   [文档](https://dev.qweather.com/docs/)
*   [图标](https://icons.qweather.com/)
*   [展示](https://dev.qweather.com/showcase/)
*   [帮助](https://dev.qweather.com/help/)
*   [价格](https://dev.qweather.com/price/)
*   Search K 

*   [文档](https://dev.qweather.com/docs/)
*   >
*   [实用资料](https://dev.qweather.com/docs/resource/)
*   >
*   [元数据](https://dev.qweather.com/docs/resource/metadata/)

#### [开始使用](https://dev.qweather.com/docs/start/)

*   [特性](https://dev.qweather.com/docs/features/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [帐号管理](https://dev.qweather.com/docs/account/)

#### [开发文档](https://dev.qweather.com/docs/api/)

*   [GeoAPI](https://dev.qweather.com/docs/api/geoapi/)
*   [天气预报](https://dev.qweather.com/docs/api/weather/)
*   [分钟预报](https://dev.qweather.com/docs/api/minutely/)
*   [预警](https://dev.qweather.com/docs/api/warning/)
*   [天气指数](https://dev.qweather.com/docs/api/indices/)
*   [空气质量](https://dev.qweather.com/docs/api/air-quality/)
*   [时光机](https://dev.qweather.com/docs/api/time-machine/)
*   [热带气旋（台风）](https://dev.qweather.com/docs/api/tropical-cyclone/)
*   [海洋数据](https://dev.qweather.com/docs/api/ocean/)
*   [太阳辐射](https://dev.qweather.com/docs/api/solar-radiation/)
*   [天文](https://dev.qweather.com/docs/api/astronomy/)
*   [控制台API](https://dev.qweather.com/docs/api/console/)

#### [实用资料](https://dev.qweather.com/docs/resource/)

*   [专用词汇表](https://dev.qweather.com/docs/resource/glossary/)
*   [错误码](https://dev.qweather.com/docs/resource/error-code/)
*   [多语言](https://dev.qweather.com/docs/resource/language/)
*   [常见城市列表](https://dev.qweather.com/docs/resource/location-list/)
*   [元数据](https://dev.qweather.com/docs/resource/metadata/)
*   [单位](https://dev.qweather.com/docs/resource/unit/)

#### [最佳实践](https://dev.qweather.com/docs/best-practices/)

*   [不要假设](https://dev.qweather.com/docs/best-practices/no-assumptions/)
*   [优化请求](https://dev.qweather.com/docs/best-practices/optimize-requests/)
*   [处理Gzip](https://dev.qweather.com/docs/best-practices/gzip/)
*   [缓存你的数据](https://dev.qweather.com/docs/best-practices/cache/)
*   [安全指南](https://dev.qweather.com/docs/best-practices/security-guidelines/)

#### [财务与费用](https://dev.qweather.com/docs/finance/)

*   [计费方式和支付](https://dev.qweather.com/docs/finance/billing-and-payment/)
*   [按量计费定价](https://dev.qweather.com/docs/finance/pricing/)
*   [节省计划](https://dev.qweather.com/docs/finance/savings-plans/)
*   [增值税发票](https://dev.qweather.com/docs/finance/vat-invoice/)

#### [条款](https://dev.qweather.com/docs/terms/)

*   [使用限制](https://dev.qweather.com/docs/terms/restriction/)
*   [注明来源](https://dev.qweather.com/docs/terms/attribution/)
*   [服务条款](https://dev.qweather.com/docs/terms/tos/)

# 元数据

API 响应中我们使用 `metadata` 对象来描述当前请求/响应的元数据信息。

## tag[#](https://dev.qweather.com/docs/resource/metadata#tag)

`tag` 是当前数据的唯一标识，用于追溯数据来源，也可以方便开发者判断数据是否更新。

## attributions[#](https://dev.qweather.com/docs/resource/metadata#attributions)

`attributions[]` 是当前数据的归因信息或声明，根据我们的开发者许可协议以及其他版权方的要求，你需要将此数组中的内容显示在你的产品或服务中。

## zeroResult[#](https://dev.qweather.com/docs/resource/metadata#zeroresult)

`zeroResult` 表示当前是否有返回的数据。当 `"zeroResult": true` 时，表示请求是成功的，但没有任何需要返回的数据。例如查询一个坐标的天气预警信息，此时该地点没有任何预警信息。

#### 本页导航

*   [tag](https://dev.qweather.com/docs/resource/metadata#tag)
*   [attributions](https://dev.qweather.com/docs/resource/metadata#attributions)
*   [zeroResult](https://dev.qweather.com/docs/resource/metadata#zeroresult)

### [和风天气开发者服务](https://dev.qweather.com/)

[联系](https://www.qweather.com/contact/)• [博客](https://blog.qweather.com/)• [GitHub](https://github.com/qwd)

Language 

### 文档

*   [开始使用](https://dev.qweather.com/docs/start/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [开发文档](https://dev.qweather.com/docs/api/)
*   [财务与费用](https://dev.qweather.com/docs/finance/)
*   [最佳实践](https://dev.qweather.com/docs/best-practices/)
*   [实用资料](https://dev.qweather.com/docs/resource/)

### 特性

*   [服务和数据](https://dev.qweather.com/docs/features/service-and-data/)
*   [全球部署](https://dev.qweather.com/docs/features/global-deployment/)
*   [性能](https://dev.qweather.com/docs/features/performance/)

### 更多

*   [天气预报](https://www.qweather.com/)
*   [天气地图](https://map.qweather.com/)
*   [下载APP](https://www.qweather.com/app/)

*   © 2026 [QWeather](https://www.qweather.com/en/)
*   [许可协议](https://www.qweather.com/terms/developers-eula/)
*   [隐私政策](https://www.qweather.com/terms/privacy)
*   [免责声明](https://www.qweather.com/terms/disclaimer/)

[京ICP备15048401号-11](https://beian.miit.gov.cn/)

<!-- END_DOC_87 -->

---


<!-- START_DOC_88: api_astronomy_moon-and-moon-phase.md (URL: https://dev.qweather.com/docs/api/astronomy/moon-and-moon-phase) -->
Title: 月升月落和月相

URL Source: https://dev.qweather.com/docs/api/astronomy/moon-and-moon-phase

Markdown Content:
[和风天气开发者服务](https://dev.qweather.com/)

[控制台](https://id.qweather.com/#/login?redirect=https://console.qweather.com)

*   [首页](https://dev.qweather.com/)
*   [文档](https://dev.qweather.com/docs/)
*   [图标](https://icons.qweather.com/)
*   [展示](https://dev.qweather.com/showcase/)
*   [帮助](https://dev.qweather.com/help/)
*   [价格](https://dev.qweather.com/price/)
*   Search K 

*   [文档](https://dev.qweather.com/docs/)
*   >
*   [开发文档](https://dev.qweather.com/docs/api/)
*   >
*   [天文](https://dev.qweather.com/docs/api/astronomy/)
*   >
*   [月升月落和月相](https://dev.qweather.com/docs/api/astronomy/moon-and-moon-phase/)

#### [开始使用](https://dev.qweather.com/docs/start/)

*   [特性](https://dev.qweather.com/docs/features/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [帐号管理](https://dev.qweather.com/docs/account/)

#### [开发文档](https://dev.qweather.com/docs/api/)

*   [GeoAPI](https://dev.qweather.com/docs/api/geoapi/)
*   [天气预报](https://dev.qweather.com/docs/api/weather/)
*   [分钟预报](https://dev.qweather.com/docs/api/minutely/)
*   [预警](https://dev.qweather.com/docs/api/warning/)
*   [天气指数](https://dev.qweather.com/docs/api/indices/)
*   [空气质量](https://dev.qweather.com/docs/api/air-quality/)
*   [时光机](https://dev.qweather.com/docs/api/time-machine/)
*   [热带气旋（台风）](https://dev.qweather.com/docs/api/tropical-cyclone/)
*   [海洋数据](https://dev.qweather.com/docs/api/ocean/)
*   [太阳辐射](https://dev.qweather.com/docs/api/solar-radiation/)
*   [天文](https://dev.qweather.com/docs/api/astronomy/)

    *   [GET 日出日落](https://dev.qweather.com/docs/api/astronomy/sunrise-sunset/)
    *   [GET 月升月落和月相](https://dev.qweather.com/docs/api/astronomy/moon-and-moon-phase/)
    *   [GET 太阳高度角](https://dev.qweather.com/docs/api/astronomy/solar-elevation-angle/)
    *   [了解太阳数据](https://dev.qweather.com/docs/api/astronomy/sun-guide/)
    *   [了解月亮数据](https://dev.qweather.com/docs/api/astronomy/moon-guide/)

*   [控制台API](https://dev.qweather.com/docs/api/console/)

#### [实用资料](https://dev.qweather.com/docs/resource/)

*   [专用词汇表](https://dev.qweather.com/docs/resource/glossary/)
*   [错误码](https://dev.qweather.com/docs/resource/error-code/)
*   [多语言](https://dev.qweather.com/docs/resource/language/)
*   [常见城市列表](https://dev.qweather.com/docs/resource/location-list/)
*   [元数据](https://dev.qweather.com/docs/resource/metadata/)
*   [单位](https://dev.qweather.com/docs/resource/unit/)

#### [最佳实践](https://dev.qweather.com/docs/best-practices/)

*   [不要假设](https://dev.qweather.com/docs/best-practices/no-assumptions/)
*   [优化请求](https://dev.qweather.com/docs/best-practices/optimize-requests/)
*   [处理Gzip](https://dev.qweather.com/docs/best-practices/gzip/)
*   [缓存你的数据](https://dev.qweather.com/docs/best-practices/cache/)
*   [安全指南](https://dev.qweather.com/docs/best-practices/security-guidelines/)

#### [财务与费用](https://dev.qweather.com/docs/finance/)

*   [计费方式和支付](https://dev.qweather.com/docs/finance/billing-and-payment/)
*   [按量计费定价](https://dev.qweather.com/docs/finance/pricing/)
*   [节省计划](https://dev.qweather.com/docs/finance/savings-plans/)
*   [增值税发票](https://dev.qweather.com/docs/finance/vat-invoice/)

#### [条款](https://dev.qweather.com/docs/terms/)

*   [使用限制](https://dev.qweather.com/docs/terms/restriction/)
*   [注明来源](https://dev.qweather.com/docs/terms/attribution/)
*   [服务条款](https://dev.qweather.com/docs/terms/tos/)

# 月升月落和月相

获取全球城市未来60天的月升月落时间和逐小时月相数据。 
## 请求路径[#](https://dev.qweather.com/docs/api/astronomy/moon-and-moon-phase#endpoint)

`GET /v7/astronomy/moon`

## 参数[#](https://dev.qweather.com/docs/api/astronomy/moon-and-moon-phase#parameters)

#### 查询参数

*   location 必选 string  需要查询地区的[LocationID](https://dev.qweather.com/docs/resource/glossary/#locationid)或以英文逗号分隔的[经度,纬度坐标](https://dev.qweather.com/docs/resource/glossary/#coordinate)（十进制，最多支持小数点后两位），LocationID可通过[GeoAPI](https://dev.qweather.com/docs/api/geoapi/)获取。例如 `location=101010100` 或 `location=116.41,39.92`  
*   date 必选 date  选择日期，最多可选择未来60天（包含今天）的数据。日期格式为yyyyMMdd，例如 `date=20200531`  
*   lang string  [多语言设置](https://dev.qweather.com/docs/resource/language/)  

## 请求示例[#](https://dev.qweather.com/docs/api/astronomy/moon-and-moon-phase#request-example)

```bash
curl -X GET --compressed \
-H 'Authorization: Bearer your_token' \
'https://your-api-host/v7/astronomy/moon?location=116.41%2C39.92&date=20200531'
```

请将`your_token`替换为你的[JWT身份认证](https://dev.qweather.com/docs/configuration/authentication/)，将`your_api_host`替换为你的[API Host](https://dev.qweather.com/docs/configuration/api-host/)

[试一下](https://dev.qweather.com/api-explore/#/Astronomy/getAstronomyMoon)

## 返回数据[#](https://dev.qweather.com/docs/api/astronomy/moon-and-moon-phase#response)

```json
{
  "code": "200",
  "updateTime": "2021-11-15T17:00+08:00",
  "fxLink": "https://www.qweather.com",
  "moonrise": "2021-11-20T17:25+08:00",
  "moonset": "2021-11-21T07:42+08:00",
  "moonPhase": [
    {
      "fxTime": "2021-11-20T00:00+08:00",
      "value": "0.51",
      "name": "亏凸月",
      "illumination": "100",
      "icon": "805"
    },
    {
      "fxTime": "2021-11-20T01:00+08:00",
      "value": "0.51",
      "name": "亏凸月",
      "illumination": "100",
      "icon": "805"
    },
    {
      "fxTime": "2021-11-20T02:00+08:00",
      "value": "0.51",
      "name": "亏凸月",
      "illumination": "100",
      "icon": "805"
    },
    {
      "fxTime": "2021-11-20T03:00+08:00",
      "value": "0.51",
      "name": "亏凸月",
      "illumination": "100",
      "icon": "805"
    },
    {
      "fxTime": "2021-11-20T04:00+08:00",
      "value": "0.52",
      "name": "亏凸月",
      "illumination": "100",
      "icon": "805"
    },
    {
      "fxTime": "2021-11-20T05:00+08:00",
      "value": "0.52",
      "name": "亏凸月",
      "illumination": "100",
      "icon": "805"
    },
    {
      "fxTime": "2021-11-20T06:00+08:00",
      "value": "0.52",
      "name": "亏凸月",
      "illumination": "100",
      "icon": "805"
    },
    {
      "fxTime": "2021-11-20T07:00+08:00",
      "value": "0.52",
      "name": "亏凸月",
      "illumination": "100",
      "icon": "805"
    },
    {
      "fxTime": "2021-11-20T08:00+08:00",
      "value": "0.52",
      "name": "亏凸月",
      "illumination": "100",
      "icon": "805"
    },
    {
      "fxTime": "2021-11-20T09:00+08:00",
      "value": "0.52",
      "name": "亏凸月",
      "illumination": "100",
      "icon": "805"
    },
    {
      "fxTime": "2021-11-20T10:00+08:00",
      "value": "0.52",
      "name": "亏凸月",
      "illumination": "99",
      "icon": "805"
    },
    {
      "fxTime": "2021-11-20T11:00+08:00",
      "value": "0.52",
      "name": "亏凸月",
      "illumination": "99",
      "icon": "805"
    },
    {
      "fxTime": "2021-11-20T12:00+08:00",
      "value": "0.53",
      "name": "亏凸月",
      "illumination": "99",
      "icon": "805"
    },
    {
      "fxTime": "2021-11-20T13:00+08:00",
      "value": "0.53",
      "name": "亏凸月",
      "illumination": "99",
      "icon": "805"
    },
    {
      "fxTime": "2021-11-20T14:00+08:00",
      "value": "0.53",
      "name": "亏凸月",
      "illumination": "99",
      "icon": "805"
    },
    {
      "fxTime": "2021-11-20T15:00+08:00",
      "value": "0.53",
      "name": "亏凸月",
      "illumination": "99",
      "icon": "805"
    },
    {
      "fxTime": "2021-11-20T16:00+08:00",
      "value": "0.53",
      "name": "亏凸月",
      "illumination": "99",
      "icon": "805"
    },
    {
      "fxTime": "2021-11-20T17:00+08:00",
      "value": "0.53",
      "name": "亏凸月",
      "illumination": "99",
      "icon": "805"
    },
    {
      "fxTime": "2021-11-20T18:00+08:00",
      "value": "0.53",
      "name": "亏凸月",
      "illumination": "99",
      "icon": "805"
    },
    {
      "fxTime": "2021-11-20T19:00+08:00",
      "value": "0.53",
      "name": "亏凸月",
      "illumination": "99",
      "icon": "805"
    },
    {
      "fxTime": "2021-11-20T20:00+08:00",
      "value": "0.54",
      "name": "亏凸月",
      "illumination": "99",
      "icon": "805"
    },
    {
      "fxTime": "2021-11-20T21:00+08:00",
      "value": "0.54",
      "name": "亏凸月",
      "illumination": "99",
      "icon": "805"
    },
    {
      "fxTime": "2021-11-20T22:00+08:00",
      "value": "0.54",
      "name": "亏凸月",
      "illumination": "99",
      "icon": "805"
    },
    {
      "fxTime": "2021-11-20T23:00+08:00",
      "value": "0.54",
      "name": "亏凸月",
      "illumination": "98",
      "icon": "805"
    }
  ],
  "refer": {
    "sources": [
      "https://developer.qweather.com/attribution.html"
    ],
    "license": [
      "QWeather Developers License"
    ]
  }
}
```

*   code string  [状态码](https://dev.qweather.com/docs/resource/error-code/)  
*   updateTime date-time  [API的最近更新时间](https://dev.qweather.com/docs/resource/glossary/#update-time)  
*   fxLink uri  当前数据的响应式页面，便于嵌入网站或应用  
*   moonrise string  当天[月升时间](https://dev.qweather.com/docs/api/astronomy/moon-guide/#moonrise-and-moonset)，可能为空  
*   moonset string  当天[月落时间](https://dev.qweather.com/docs/api/astronomy/moon-guide/#moonrise-and-moonset)，可能为空  
*   moonPhase array  月相数据列表  
    *   fxTime date-time  月相逐小时预报时间  
    *   value string  月相数值  
    *   name string  [月相名称](https://dev.qweather.com/docs/api/astronomy/moon-guide/#moon-phase)  
    *   illumination string  月亮照明度，百分比数值  
    *   icon string  月相[图标代码](https://dev.qweather.com/docs/api/weather/weather-conditions/#icons)，另请参考[天气图标项目](https://icons.qweather.com/)  

*   refer object  数据来源和许可信息  
    *   sources array  原始数据来源，或数据源说明，可能为空  

    *   license array  数据许可或版权声明，可能为空  

#### 本页导航

*   [请求路径](https://dev.qweather.com/docs/api/astronomy/moon-and-moon-phase#endpoint)
*   [参数](https://dev.qweather.com/docs/api/astronomy/moon-and-moon-phase#parameters)
*   [请求示例](https://dev.qweather.com/docs/api/astronomy/moon-and-moon-phase#request-example)
*   [返回数据](https://dev.qweather.com/docs/api/astronomy/moon-and-moon-phase#response)

### [和风天气开发者服务](https://dev.qweather.com/)

[联系](https://www.qweather.com/contact/)• [博客](https://blog.qweather.com/)• [GitHub](https://github.com/qwd)

Language 

### 文档

*   [开始使用](https://dev.qweather.com/docs/start/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [开发文档](https://dev.qweather.com/docs/api/)
*   [财务与费用](https://dev.qweather.com/docs/finance/)
*   [最佳实践](https://dev.qweather.com/docs/best-practices/)
*   [实用资料](https://dev.qweather.com/docs/resource/)

### 特性

*   [服务和数据](https://dev.qweather.com/docs/features/service-and-data/)
*   [全球部署](https://dev.qweather.com/docs/features/global-deployment/)
*   [性能](https://dev.qweather.com/docs/features/performance/)

### 更多

*   [天气预报](https://www.qweather.com/)
*   [天气地图](https://map.qweather.com/)
*   [下载APP](https://www.qweather.com/app/)

*   © 2026 [QWeather](https://www.qweather.com/en/)
*   [许可协议](https://www.qweather.com/terms/developers-eula/)
*   [隐私政策](https://www.qweather.com/terms/privacy)
*   [免责声明](https://www.qweather.com/terms/disclaimer/)

[京ICP备15048401号-11](https://beian.miit.gov.cn/)

<!-- END_DOC_88 -->

---


<!-- START_DOC_89: finance_vat-invoice.md (URL: https://dev.qweather.com/docs/finance/vat-invoice) -->
Title: 增值税发票

URL Source: https://dev.qweather.com/docs/finance/vat-invoice

Published Time: Thu, 27 Aug 2026 02:08:36 GMT

Markdown Content:
## 增值税发票

对于中国地区的用户，和风天气开发服务的费用均已包含增值税，你可以在支付账单后申请开具增值税发票。

> **注意：** 请认真阅读本篇文档，了解我们如何向你开具增值税发票、增值税发票的类型、开票时间以及开票限制，必要时请将本篇文档发送给你的财务人员查看。

## 开票金额[#](https://dev.qweather.com/docs/finance/vat-invoice#invoice-amount)

开票金额根据已支付的账单金额确定，例如你的按量计费账单金额是123.45元，那么当你支付这个账单后，可以申请123.45元的增值税发票。

充值到可用额度的正数金额不支持开具发票，你必须使用可用额度支付账单后，根据账单金额申请增值税发票。例如你充值了1000元到可用额度，然后使用可用额度支付了一个600元的节省计划和上个月的按量计费账单200元，此时你可以申请增值税发票的金额是 800元 = 600元（节省计划账单金额）+ 200元（按量计费账单金额）

#### 不可开票金额

如果你的帐号存在不可开票金额，你所选择的账单金额将减去不可开票金额作为最终的开票金额：

```
可开票金额 = 账单金额 - 不可开票金额
```

不可开票金额产生的原因包括：

*   在2022年10月8日之前对充值金额开具了发票，但是还有剩余金额未消费的。例如：在2022年10月8日之前充值100元并开具了100元发票，但在2022年10月8日之后还剩余30元可用额度未使用，则此时不可开票金额为30元。
*   对于已开具发票的服务或产品进行了退款，退款金额将为不可开票金额。

## 税率[#](https://dev.qweather.com/docs/finance/vat-invoice#tax-rate)

根据税务部门的政策，我们会调整税率，税率范围在0-3%之间，请以实际发票为准。

## 发票项目[#](https://dev.qweather.com/docs/finance/vat-invoice#invoice-item)

信息技术服务/软件服务/气象服务

## 发票格式[#](https://dev.qweather.com/docs/finance/vat-invoice#invoice-format)

**全面数字化的电子发票**（数电发票），其法律效力、基本用途与现有增值税发票相同，相关政策请参考[《国家税务总局北京市税务局关于开展全面数字化的电子发票试点工作的公告》](https://www.beijing.gov.cn/zhengce/zhengcefagui/202312/t20231207_3493065.html)。

数电发票参考样例：

*   [增值税普通发票](https://dev.qweather.com/assets/images/content/vat-sample.jpg)
*   [增值税专用发票](https://dev.qweather.com/assets/images/content/vat-s-sample.jpg)

## 申请发票[#](https://dev.qweather.com/docs/finance/vat-invoice#apply-for-vat-invoice)

申请发票之前，你需要先添加发票信息。如果你是企业开发者且只需要增值税普通发票，则可以跳过这一步。

> **提示：** 请务必与你的财务人员确认企业信息，建议复制粘贴企业信息，避免打字错误。

1.   [前往控制台-发票信息](https://console.qweather.com/finance/vat-invoice/info)
2.   填写基本信息，如需要增值税专用发票，还需要填写扩展信息
3.   点击“保存”按钮

添加发票信息后，可以继续申请发票。

1.   [前往控制台-增值税发票](https://console.qweather.com/finance/vat-invoice)
2.   点击可开票金额卡片中的“申请发票”按钮
3.   选择你需要开具发票的账单，可以选择一个账单或多个账单合并开具发票，点击“下一步”按钮
4.   选择发票类型（增值税专用发票仅限企业开发者申请），检查发票信息，填写联系人信息。如你有其他额外开票信息，请填写在备注栏。
5.   最后点击“申请发票”按钮

#### 送达和下载

在申请开票后，10个工作日内将发送到你的电子邮箱，你也可以在[控制台 - 增值税发票](https://console.qweather.com/finance/vat-invoice)中查看并下载你的发票。

财务人员也可通过税务数字账户或增值税发票综合服务平台下载和查询。

#### 修改发票

在发票开具之前，你可以随时取消申请发票：

1.   [前往控制台-增值税发票](https://console.qweather.com/finance/vat-invoice)
2.   点击需要修改或取消的发票
3.   在页面下方点击**取消申请发票**按钮

如发票开具后出现错误或需要修改，请[提交工单](https://console.qweather.com/support/ticket)进行修改，每年度仅限修改3次。

> **提示：** 请在开具发票完成后的12个月内提出修改申请，逾期不再受理。

## 限制[#](https://dev.qweather.com/docs/finance/vat-invoice#restriction)

*   如有逾期欠款状态的账单，无法申请发票，请先完成欠款账单的支付。
*   如果帐号被冻结，无法申请发票。
*   超过12个月的账单，无法申请发票。
*   增值税专用发票仅限[企业开发者](https://dev.qweather.com/docs/account/developers/)申请。

<!-- END_DOC_89 -->

---


<!-- START_DOC_90: account_suspension.md (URL: https://dev.qweather.com/docs/account/suspension) -->
Title: 帐号冻结

URL Source: https://dev.qweather.com/docs/account/suspension

Markdown Content:
## 帐号冻结

当你的行为违反了和风天气的[各项条款](https://dev.qweather.com/docs/terms/)，你的帐号可能将被冻结，冻结后你将无法操作你帐号中的大部分功能，并且和风天气保留对你的违规行为要求赔偿的权利，严重时你将面临法律风险。

## 后果[#](https://dev.qweather.com/docs/account/suspension#consequences)

*   冻结帐号无法使用和风天气所有开发服务，包括之前创建的项目，所有开发请求都将被拒绝。
*   冻结帐号内如有未到期订阅或未使用的可用额度，都将暂时无法使用。
*   控制台中的所有功能将无法使用，如因为欠款账单而造成的冻结，则除充值以外的功能无法使用。
*   已绑定的支付方式可能无法在其他正常帐号中使用。

## 为什么冻结帐号[#](https://dev.qweather.com/docs/account/suspension#why-accounts-are-suspended)

和风天气开发者服务的各项条款有助于全体用户获得公平服务，并保护用户与和风天气合作伙伴的权益，因此如果你的行为违反了这些条款，你的帐号可能会被冻结。

当你的帐号被冻结时，你会收到我们的邮件通知，并告知冻结原因。一般来说，常见的冻结原因包括：

1.   **欠款账单持续30天**

如果你有一张欠款账单，你的和风天气开发服务将立即停止，你可以随时完成欠款账单的支付，和风天气的开发服务也会随即恢复。但如果该欠款账单持续30天未支付，你的帐号将被冻结。

2.   **大量错误的请求**

你的程序可能会出错，导致向和风天气开发服务发送了大量错误的请求，即返回的状态码不是`2xx`，在这种情况下我们会暂停你的帐号。

3.   **违反许可证和归因要求**

你应该遵守[开发者许可协议](https://dev.qweather.com/docs/terms/tos/)和[注明来源](https://dev.qweather.com/docs/terms/attribution/)的要求。当你违反这些条款时，你的帐号会被暂停。

4.   **提供虚假信息**

在你提供给和风天气的帐号信息中存在虚假内容，例如错误的电子邮箱、身份信息等，在这种情况下我们会暂停你的帐号。请注意，虚假信息包含临时邮箱或临时电话号码。

## 恢复和申诉[#](https://dev.qweather.com/docs/account/suspension#restore-and-appeal)

对于因欠款账单而冻结的帐号，你可以登录控制台完成欠款账单的支付，即可恢复和风天气的服务。

对于其他原因，在你修正了你的行为后，可以登录控制台提交恢复申请。

如果你认为帐号冻结是错误的，你可以登录控制台提交申诉。你的申诉，我们将认真审核，审核期为10个工作日，但不承诺必然通过或必然不通过，如果审核未通过的，我们不会做进一步的告知。

## 永久冻结[#](https://dev.qweather.com/docs/account/suspension#%e6%b0%b8%e4%b9%85%e5%86%bb%e7%bb%93)

对于冻结状态持续较长时间，或严重或多次违反我们的各项条款的帐号，将被永久冻结，用户将永久的失去此帐号的使用权且不能提起任何申诉。我们保留对永久冻结的帐号采取法律行动的权利。

<!-- END_DOC_90 -->

---


<!-- START_DOC_91: account.md (URL: https://dev.qweather.com/docs/account) -->
Title: 帐号管理

URL Source: https://dev.qweather.com/docs/account

Published Time: Thu, 27 Aug 2026 02:08:36 GMT

Markdown Content:
[和风天气开发者服务](https://dev.qweather.com/)

[控制台](https://id.qweather.com/#/login?redirect=https://console.qweather.com)

*   [首页](https://dev.qweather.com/)
*   [文档](https://dev.qweather.com/docs/)
*   [图标](https://icons.qweather.com/)
*   [展示](https://dev.qweather.com/showcase/)
*   [帮助](https://dev.qweather.com/help/)
*   [价格](https://dev.qweather.com/price/)
*   Search K 

*   [文档](https://dev.qweather.com/docs/)
*   >
*   [帐号管理](https://dev.qweather.com/docs/account/)

#### [开始使用](https://dev.qweather.com/docs/start/)

*   [特性](https://dev.qweather.com/docs/features/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [帐号管理](https://dev.qweather.com/docs/account/)

    *   [用户管理](https://dev.qweather.com/docs/account/management/)
    *   [开发者类型](https://dev.qweather.com/docs/account/developers/)
    *   [通知接收人](https://dev.qweather.com/docs/account/recipients/)
    *   [帐号冻结](https://dev.qweather.com/docs/account/suspension/)
    *   [闲置帐号](https://dev.qweather.com/docs/account/inactive/)

#### [开发文档](https://dev.qweather.com/docs/api/)

*   [GeoAPI](https://dev.qweather.com/docs/api/geoapi/)
*   [天气预报](https://dev.qweather.com/docs/api/weather/)
*   [分钟预报](https://dev.qweather.com/docs/api/minutely/)
*   [预警](https://dev.qweather.com/docs/api/warning/)
*   [天气指数](https://dev.qweather.com/docs/api/indices/)
*   [空气质量](https://dev.qweather.com/docs/api/air-quality/)
*   [时光机](https://dev.qweather.com/docs/api/time-machine/)
*   [热带气旋（台风）](https://dev.qweather.com/docs/api/tropical-cyclone/)
*   [海洋数据](https://dev.qweather.com/docs/api/ocean/)
*   [太阳辐射](https://dev.qweather.com/docs/api/solar-radiation/)
*   [天文](https://dev.qweather.com/docs/api/astronomy/)
*   [控制台API](https://dev.qweather.com/docs/api/console/)

#### [实用资料](https://dev.qweather.com/docs/resource/)

*   [专用词汇表](https://dev.qweather.com/docs/resource/glossary/)
*   [错误码](https://dev.qweather.com/docs/resource/error-code/)
*   [多语言](https://dev.qweather.com/docs/resource/language/)
*   [常见城市列表](https://dev.qweather.com/docs/resource/location-list/)
*   [元数据](https://dev.qweather.com/docs/resource/metadata/)
*   [单位](https://dev.qweather.com/docs/resource/unit/)

#### [最佳实践](https://dev.qweather.com/docs/best-practices/)

*   [不要假设](https://dev.qweather.com/docs/best-practices/no-assumptions/)
*   [优化请求](https://dev.qweather.com/docs/best-practices/optimize-requests/)
*   [处理Gzip](https://dev.qweather.com/docs/best-practices/gzip/)
*   [缓存你的数据](https://dev.qweather.com/docs/best-practices/cache/)
*   [安全指南](https://dev.qweather.com/docs/best-practices/security-guidelines/)

#### [财务与费用](https://dev.qweather.com/docs/finance/)

*   [计费方式和支付](https://dev.qweather.com/docs/finance/billing-and-payment/)
*   [按量计费定价](https://dev.qweather.com/docs/finance/pricing/)
*   [节省计划](https://dev.qweather.com/docs/finance/savings-plans/)
*   [增值税发票](https://dev.qweather.com/docs/finance/vat-invoice/)

#### [条款](https://dev.qweather.com/docs/terms/)

*   [使用限制](https://dev.qweather.com/docs/terms/restriction/)
*   [注明来源](https://dev.qweather.com/docs/terms/attribution/)
*   [服务条款](https://dev.qweather.com/docs/terms/tos/)

# 帐号管理

你需要一个和风天气的帐号用于开发服务，在这里你可以了解如何管理你的帐号以及关于帐号的基本政策。

[用户管理](https://dev.qweather.com/docs/account/management/)
了解如何注册帐号、管理用户信息、关闭开发服务以及永久删除帐号。

[开发者类型](https://dev.qweather.com/docs/account/developers/)
了解个人开发者和企业开发者的区别、选择要求，以及企业信息的变更方式。

[通知接收人](https://dev.qweather.com/docs/account/recipients/)
了解如何为帐号添加和删除通知接收人，以便其他联系人接收账单或服务通知。

[帐号冻结](https://dev.qweather.com/docs/account/suspension/)
了解帐号被冻结的原因和影响，以及恢复服务或提交申诉的方式。

[闲置帐号](https://dev.qweather.com/docs/account/inactive/)
了解帐号被视为闲置的条件和后续处理方式。

### [和风天气开发者服务](https://dev.qweather.com/)

[联系](https://www.qweather.com/contact/)• [博客](https://blog.qweather.com/)• [GitHub](https://github.com/qwd)

Language 

### 文档

*   [开始使用](https://dev.qweather.com/docs/start/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [开发文档](https://dev.qweather.com/docs/api/)
*   [财务与费用](https://dev.qweather.com/docs/finance/)
*   [最佳实践](https://dev.qweather.com/docs/best-practices/)
*   [实用资料](https://dev.qweather.com/docs/resource/)

### 特性

*   [服务和数据](https://dev.qweather.com/docs/features/service-and-data/)
*   [全球部署](https://dev.qweather.com/docs/features/global-deployment/)
*   [性能](https://dev.qweather.com/docs/features/performance/)

### 更多

*   [天气预报](https://www.qweather.com/)
*   [天气地图](https://map.qweather.com/)
*   [下载APP](https://www.qweather.com/app/)

*   © 2026 [QWeather](https://www.qweather.com/en/)
*   [许可协议](https://www.qweather.com/terms/developers-eula/)
*   [隐私政策](https://www.qweather.com/terms/privacy)
*   [免责声明](https://www.qweather.com/terms/disclaimer/)

[京ICP备15048401号-11](https://beian.miit.gov.cn/)

<!-- END_DOC_91 -->

---


<!-- START_DOC_92: api_indices_indices-type.md (URL: https://dev.qweather.com/docs/api/indices/indices-type) -->
Title: 指数类型

URL Source: https://dev.qweather.com/docs/api/indices/indices-type

Markdown Content:
了解天气指数支持的国家或地区以及天气指数的类型和等级说明。

| 天气指数 | API type | iOS type | Android type | (级别) 类别 |
| --- | --- | --- | --- | --- |
| * **全部天气指数** | `0` | `ALL` | `ALL` |  |
| * **运动指数** * 全球 | `1` | `SPT` | `SPT` | (1) 适宜 |
| (2) 较适宜 |
| (3) 较不宜 |
| * **洗车指数** * 全球 | `2` | `CW` | `CW` | (1) 适宜 |
| (2) 较适宜 |
| (3) 较不宜 |
| (4) 不宜 |
| * **穿衣指数** * 全球 | `3` | `DRSG` | `DRSG` | (1) 寒冷 |
| (2) 冷 |
| (3) 较冷 |
| (4) 较舒适 |
| (5) 舒适 |
| (6) 热 |
| (7) 炎热 |
| * **钓鱼指数** * 全球 | `4` | `FIS` | `FIS` | (1) 适宜 |
| (2) 较适宜 |
| (3) 不宜 |
| * **紫外线指数** * 全球 | `5` | `UV` | `UV` | (1) 最弱 |
| (2) 弱 |
| (3) 中等 |
| (4) 强 |
| (5) 很强 |
| * **旅游指数** * 中国 | `6` | `TRA` | `TRA` | (1) 适宜 |
| (2) 较适宜 |
| (3) 一般 |
| (4) 较不宜 |
| (5) 不适宜 |
| * **花粉过敏指数** * 中国 | `7` | `AG` | `AG` | (1) 极不易发 |
| (2) 不易发 |
| (3) 较易发 |
| (4) 易发 |
| (5) 极易发 |
| * **舒适度指数** * 中国 | `8` | `COMF` | `COMF` | (1) 舒适 |
| (2) 较舒适 |
| (3) 较不舒适 |
| (4) 很不舒适 |
| (5) 极不舒适 |
| (6) 不舒适 |
| (7) 非常不舒适 |
| * **感冒指数** * 中国 | `9` | `FLU` | `FLU` | (1) 少发 |
| (2) 较易发 |
| (3) 易发 |
| (4) 极易发 |
| * **空气污染扩散条件指数** * 中国 | `10` | `AP` | `AP` | (1) 优 |
| (2) 良 |
| (3) 中 |
| (4) 较差 |
| (5) 很差 |
| * **空调开启指数** * 中国 | `11` | `AC` | `AC` | (1) 长时间开启 |
| (2) 部分时间开启 |
| (3) 较少开启 |
| (4) 开启制暖空调 |
| * **太阳镜指数** * 中国 | `12` | `GL` | `GL` | (1) 不需要 |
| (2) 需要 |
| (3) 必要 |
| (4) 很必要 |
| (5) 非常必要 |
| * **化妆指数** * 中国 | `13` | `MU` | `MU` | (1) 保湿 |
| (2) 保湿防晒 |
| (3) 去油防晒 |
| (4) 防脱水防晒 |
| (5) 去油 |
| (6) 防脱水 |
| (7) 防晒 |
| (8) 滋润保湿 |
| * **晾晒指数** * 中国 | `14` | `DC` | `DC` | (1) 极适宜 |
| (2) 适宜 |
| (3) 基本适宜 |
| (4) 不太适宜 |
| (5) 不宜 |
| (6) 不适宜 |
| * **交通指数** * 中国 | `15` | `PTFC` | `PTFC` | (1) 良好 |
| (2) 较好 |
| (3) 一般 |
| (4) 较差 |
| (5) 很差 |
| * **防晒指数** * 中国 | `16` | `SPI` | `SPI` | (1) 弱 |
| (2) 较弱 |
| (3) 中等 |
| (4) 强 |
| (5) 极强 |

<!-- END_DOC_92 -->

---


<!-- START_DOC_93: api_air-quality_china-aqi.md (URL: https://dev.qweather.com/docs/api/air-quality/china-aqi) -->
Title: 中国空气质量说明

URL Source: https://dev.qweather.com/docs/api/air-quality/china-aqi

Markdown Content:
[和风天气开发者服务](https://dev.qweather.com/)

[控制台](https://id.qweather.com/#/login?redirect=https://console.qweather.com)

*   [首页](https://dev.qweather.com/)
*   [文档](https://dev.qweather.com/docs/)
*   [图标](https://icons.qweather.com/)
*   [展示](https://dev.qweather.com/showcase/)
*   [帮助](https://dev.qweather.com/help/)
*   [价格](https://dev.qweather.com/price/)
*   Search K 

*   [文档](https://dev.qweather.com/docs/)
*   >
*   [开发文档](https://dev.qweather.com/docs/api/)
*   >
*   [空气质量](https://dev.qweather.com/docs/api/air-quality/)
*   >
*   [中国空气质量说明](https://dev.qweather.com/docs/api/air-quality/china-aqi/)

#### [开始使用](https://dev.qweather.com/docs/start/)

*   [特性](https://dev.qweather.com/docs/features/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [帐号管理](https://dev.qweather.com/docs/account/)

#### [开发文档](https://dev.qweather.com/docs/api/)

*   [GeoAPI](https://dev.qweather.com/docs/api/geoapi/)
*   [天气预报](https://dev.qweather.com/docs/api/weather/)
*   [分钟预报](https://dev.qweather.com/docs/api/minutely/)
*   [预警](https://dev.qweather.com/docs/api/warning/)
*   [天气指数](https://dev.qweather.com/docs/api/indices/)
*   [空气质量](https://dev.qweather.com/docs/api/air-quality/)

    *   [GET 实时空气质量](https://dev.qweather.com/docs/api/air-quality/air-current/)
    *   [GET 空气质量小时预报](https://dev.qweather.com/docs/api/air-quality/air-hourly-forecast/)
    *   [GET 空气质量每日预报](https://dev.qweather.com/docs/api/air-quality/air-daily-forecast/)
    *   [支持的空气质量指数](https://dev.qweather.com/docs/api/air-quality/aqi-list/)
    *   [污染物列表](https://dev.qweather.com/docs/api/air-quality/pollutant-list/)
    *   [空气质量覆盖范围](https://dev.qweather.com/docs/api/air-quality/aqi-coverage/)
    *   [健康影响和建议](https://dev.qweather.com/docs/api/air-quality/health-effect-advice/)
    *   [中国空气质量说明](https://dev.qweather.com/docs/api/air-quality/china-aqi/)

*   [时光机](https://dev.qweather.com/docs/api/time-machine/)
*   [热带气旋（台风）](https://dev.qweather.com/docs/api/tropical-cyclone/)
*   [海洋数据](https://dev.qweather.com/docs/api/ocean/)
*   [太阳辐射](https://dev.qweather.com/docs/api/solar-radiation/)
*   [天文](https://dev.qweather.com/docs/api/astronomy/)
*   [控制台API](https://dev.qweather.com/docs/api/console/)

#### [实用资料](https://dev.qweather.com/docs/resource/)

*   [专用词汇表](https://dev.qweather.com/docs/resource/glossary/)
*   [错误码](https://dev.qweather.com/docs/resource/error-code/)
*   [多语言](https://dev.qweather.com/docs/resource/language/)
*   [常见城市列表](https://dev.qweather.com/docs/resource/location-list/)
*   [元数据](https://dev.qweather.com/docs/resource/metadata/)
*   [单位](https://dev.qweather.com/docs/resource/unit/)

#### [最佳实践](https://dev.qweather.com/docs/best-practices/)

*   [不要假设](https://dev.qweather.com/docs/best-practices/no-assumptions/)
*   [优化请求](https://dev.qweather.com/docs/best-practices/optimize-requests/)
*   [处理Gzip](https://dev.qweather.com/docs/best-practices/gzip/)
*   [缓存你的数据](https://dev.qweather.com/docs/best-practices/cache/)
*   [安全指南](https://dev.qweather.com/docs/best-practices/security-guidelines/)

#### [财务与费用](https://dev.qweather.com/docs/finance/)

*   [计费方式和支付](https://dev.qweather.com/docs/finance/billing-and-payment/)
*   [按量计费定价](https://dev.qweather.com/docs/finance/pricing/)
*   [节省计划](https://dev.qweather.com/docs/finance/savings-plans/)
*   [增值税发票](https://dev.qweather.com/docs/finance/vat-invoice/)

#### [条款](https://dev.qweather.com/docs/terms/)

*   [使用限制](https://dev.qweather.com/docs/terms/restriction/)
*   [注明来源](https://dev.qweather.com/docs/terms/attribution/)
*   [服务条款](https://dev.qweather.com/docs/terms/tos/)

# 中国空气质量说明

在中国地区的空气质量数据，请参考下列说明：

*   空气质量指数的计算遵循[《环境空气质量指数（AQI）技术规定》（HJ 633—2026）](https://www.mee.gov.cn/ywgz/fgbz/bz/bzwb/jcffbz/202602/t20260225_1144441.shtml)
*   暂不支持 QAQI
*   空气质量预报中，不支持污染物的详细数据
*   当污染物分指数<50时，AQI (CN) 和 AQI-1H (CN) 的首要污染物均为空
*   空气质量指数基于数值模型和观测站数据计算而来，**未经过完整的审核程序进行修订和确认，不适用评价达标状况或任何正式评估，仅为参考值**

### [和风天气开发者服务](https://dev.qweather.com/)

[联系](https://www.qweather.com/contact/)• [博客](https://blog.qweather.com/)• [GitHub](https://github.com/qwd)

Language 

### 文档

*   [开始使用](https://dev.qweather.com/docs/start/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [开发文档](https://dev.qweather.com/docs/api/)
*   [财务与费用](https://dev.qweather.com/docs/finance/)
*   [最佳实践](https://dev.qweather.com/docs/best-practices/)
*   [实用资料](https://dev.qweather.com/docs/resource/)

### 特性

*   [服务和数据](https://dev.qweather.com/docs/features/service-and-data/)
*   [全球部署](https://dev.qweather.com/docs/features/global-deployment/)
*   [性能](https://dev.qweather.com/docs/features/performance/)

### 更多

*   [天气预报](https://www.qweather.com/)
*   [天气地图](https://map.qweather.com/)
*   [下载APP](https://www.qweather.com/app/)

*   © 2026 [QWeather](https://www.qweather.com/en/)
*   [许可协议](https://www.qweather.com/terms/developers-eula/)
*   [隐私政策](https://www.qweather.com/terms/privacy)
*   [免责声明](https://www.qweather.com/terms/disclaimer/)

[京ICP备15048401号-11](https://beian.miit.gov.cn/)

<!-- END_DOC_93 -->

---


<!-- START_DOC_94: terms_attribution.md (URL: https://dev.qweather.com/docs/terms/attribution) -->
Title: 注明来源

URL Source: https://dev.qweather.com/docs/terms/attribution

Markdown Content:
根据我们的[开发者许可协议](https://www.qweather.com/terms/developers-eula)和数据源方的版权要求，你需要在使用了和风天气服务的产品中注明来源（或称之为“归因”）。例如你在APP中的某个页面显示了来自和风天气的内容，你应该在此页面显示“天气服务由和风天气驱动”的类似字样。

对于不同的数据，请根据下方规范注明来源：

## 注明来源[#](https://dev.qweather.com/docs/terms/attribution#attribution-requirements)

如果在你的产品中（例如APP、网站等）使用了和风天气开发服务，你需要清晰的展示我们的名字：`和风天气`或`QWeather`，并添加超级链`https://www.qweather.com`。我们推荐：

```
天气服务由和风天气驱动
```

除此之外，如果你使用到了下列数据，还需要额外添加来源信息。

**天气预警**

你必须在使用了天气预警信息的产品中完整的显示`refer.sources`的所有内容，并且不能以任何形式修改此内容。

**空气质量**

你必须在使用了空气质量的产品中完整的显示`refer.sources`的所有内容，并且不能以任何形式修改此内容。

## 样式要求[#](https://dev.qweather.com/docs/terms/attribution#attribution-style)

在显示来源的样式上，我们没有明确的要求，优先以你的产品样式确定，但请遵循下列指导：

*   **可见性：** 请保证来源显示的可见性，即字体样式、字号或颜色不应该过于难以辨认。
*   **不易混淆：** 来源应该是单独放置的，而不是与你产品的内容混为一体的，或容易引起误解的。例如“天气服务由和风天气驱动”不应该出现在网页正文的某一个段落里，或紧挨着一则广告（避免被误解为该广告由来源方提供）。
*   **非冒犯性：** 在注明来源的上下文中，不能出现冒犯性或容易引起反感或对来源方产生负面影响的内容。

## 例外[#](https://dev.qweather.com/docs/terms/attribution#exceptions)

如果你使用的是我们单独提供的商业许可授权，可能不适用本文档的约定，或者如果你的产品在特殊设备上无法显示来源字样，请与我们联系，以便讨论此类特例情况的处理方式。

<!-- END_DOC_94 -->

---


<!-- START_DOC_95: resource_glossary.md (URL: https://dev.qweather.com/docs/resource/glossary) -->
Title: 专用词汇表

URL Source: https://dev.qweather.com/docs/resource/glossary

Markdown Content:
## 专用词汇表

## Rank[#](https://dev.qweather.com/docs/resource/glossary#rank)

Rank值表示城市或地区的相对重要性，基于人口、面积、GDP、搜索热度等因素综合计算。在地理信息API的响应中，返回结果会在关键字相关性之外参考该地点的`Rank`值。Rank 的取值范围为 1 到 100，数值越小表示重要性越高。例如，当使用“西安”作为关键字时，西安市的 Rank 值通常比西安区更小，因此会排在更靠前的位置。

## 坐标[#](https://dev.qweather.com/docs/resource/glossary#coordinate)

*   中国大陆地区应使用GCJ-02坐标系，在其他地区应使用WGS-84坐标系。
*   查询格式：`经度,纬度`（经度在前纬度在后，英文逗号分隔，十进制格式，北纬东经为正，南纬西经为负）。例如：`location=116.41,39.92`

## 行政区划[#](https://dev.qweather.com/docs/resource/glossary#administrative)

在全球各个国家中，都有不同的行政等级划分，为了便于理解和方便查询到正确的的天气信息，和风天气的所有城市都会属于至少2个行政区划等级。请注意，和风天气的行政区划等级不完全等于各国的实际行政区划等级。

*   代码`adm1`，代表一级行政区划，一般为各国的一级行政区划，例如省/州/府/构成国，是国家之下的第一个行政区划，也包括直辖市/首都/特区等特殊城市。
*   代码`adm2`，代表次级行政区划，一般为具体城市或地区的直属行政区划，例如余杭区的次级行政区划是杭州市，曼哈顿区的次级行政区划是纽约市。如果一个城市的直属行政区划等于一级行政区划，则这个城市的直属上级行政区划就是该城市本身，例如深圳市的一级行政区划是广东省，次级行政区划是深圳市。

## LocationID[#](https://dev.qweather.com/docs/resource/glossary#locationid)

LocationID或locid，是城市、地区或POI点的ID，一般由数字或字母+数字组成，是一个地点的唯一标识。LocationID可以通过定位搜索服务获取，中国地区、热门海外城市、一些POI点的LocationID还可以通过[城市列表](https://dev.qweather.com/docs/resource/location-list/)下载。

## Adcode[#](https://dev.qweather.com/docs/resource/glossary#adcode)

Adcode是中国行政区域编码。

## ISO 3166[#](https://dev.qweather.com/docs/resource/glossary#iso-3166)

[ISO 3166](https://www.iso.org/iso-3166-country-codes.html)是国际标准化组织（ISO）发布的全球国家代码标准，正式名称是国家名称和其分支的代码。该标准规定了国家名称、附属领土、地理利益的特殊区域以及它们的主要分支（如省或州）的名称。国家代码有二位字母代码、三位字母代码、以及三位数字代码，和风天气采用的是其中的二位字母代码（alpha-2）。

完整的二位数字ISO 3166可[通过我们的Github下载](https://github.com/qwd/LocationList)。

## POI[#](https://dev.qweather.com/docs/resource/glossary#poi)

Point of Interest，兴趣点。在和风天气的兴趣点概念中包括：机场、港口、火车站、中国空气质量监测站和中国景点。

## 日期和时间[#](https://dev.qweather.com/docs/resource/glossary#date-time)

和风天气在v7版本及以上使用[ISO8601:2004](https://en.wikipedia.org/wiki/ISO_8601)作为所有日期和时间表达格式。日期和时间均为当地时间。

*   日期格式：YYYY-MM-DD，例如 2019-12-30，代表2019年12月30日。
*   长时间格式：YYYY-MM-DDTHH:MM±timezone，例如 2019-12-30T08:35+0800，代表东八区2019年12月30日上午8点35分。
*   短时间格式：HH:MM，例如 20:31

## Timezone[#](https://dev.qweather.com/docs/resource/glossary#timezone)

Timezone（时区），在API中的字段名为`tz`，该时区的表达规范采用了tz database格式，或称[IANA time zone database](https://www.iana.org/time-zones)。

## 夏令时[#](https://dev.qweather.com/docs/resource/glossary#daylight-saving-time)

isDST表示当前是否处于夏令时。天气数据中的时间已经对夏令时做了调整，你不需要再进行任何转换。

## UTC Offset[#](https://dev.qweather.com/docs/resource/glossary#utc-offset)

在城市搜索接口中，`utcOffset`字段表示该地区/城市当前时间与UTC时间的偏移，该偏移已经考虑了一些国家在进入夏令时的情况，因此这个偏移与时区偏移不完全一样。例如纽约的时区是America/New_York，与UTC相差-5小时，当纽约在每年3月的第二个星期日进入夏令时后，纽约当地时间与UTC相差-4小时，此时`utcOffset`的值是`-04:00`

## Public Time[#](https://dev.qweather.com/docs/resource/glossary#public-time)

数据发布时间，是观测站或数据源发布的时间，代表当前数据是在什么时刻发布的。

## Update Time[#](https://dev.qweather.com/docs/resource/glossary#update-time)

和风天气数据更新时间，与pubTime不同，该时间是和风天气系统进行数据更新的时间，表示当前获取的数据最近一次更新的时间。

*   可以通过相同的更新时间来判断数据内容是否有变化，但不能依靠不同的更新时间来判断数据内容一定有变化，在有些情况下，气象模型计算结果有可能在不同的更新时间得到相同的结果。
*   由于天气数据的特殊性以及数据更新的持续性，无法确定精确的更新时间，请以接口返回数据的`updateTime`字段为准。

| 数据 | 更新间隔 |
| --- | --- |
| 灾害预警 | 5分钟 |
| 逐天预报 | 1-8小时 |
| 逐小时预报 | 1小时 |
| 生活指数 | 1小时 |
| 实况类数据 | 10-40分钟 |
| 分钟级降雨 | 10-20分钟 |
| 卫星云图 | 30-60分钟 |

## iOS Bundle identifier[#](https://dev.qweather.com/docs/resource/glossary#ios-bundle-identifier)

Xcode切换到Info下，可查看Bundle Identifier。

请注意Bundle ID仅限英文字母、数字、短横线和下划线。

## Android Package Name[#](https://dev.qweather.com/docs/resource/glossary#android-package-name)

打开Android 应用工程的**AndroidManifest.xml**配置文件，**package**属性所对应的内容为应用包名。

请注意Android Package Name仅限英文字母、数字、短横线和下划线。

## 最大连接数[#](https://dev.qweather.com/docs/resource/glossary#max-connections)

API或SDK服务能够承载的最大连接数量。当连接超过当前应用版本所限制的最大连接数时，新建连接请求将被丢弃。

## QPM[#](https://dev.qweather.com/docs/resource/glossary#qpm)

QPM (Query Per Minute)，每分钟请求数，超过每分钟请求数量的请求将返回错误代码，在下一分钟后恢复。

*   按量计费订阅: 3000
*   高级订阅: 50000+

QPM以单独项目计算，一个项目下所有数据KEY共享QPM，不同项目分别计算QPM。

## 风力等级[#](https://dev.qweather.com/docs/resource/glossary#wind-scale)

风力等级用于直接展示，不建议作为枚举项使用；例如以后出现更高等级风力或者变更了风力等级的标准，采用枚举项的方式则会出现匹配不到的情况，此情况请谨慎使用。

风力等级依据风速而来，可参考[风力等级与风速对照](https://dev.qweather.com/docs/api/weather/wind-guide/)，和风天气采用 KM/H 为单位。

当风力等级为0时，风向角度为-1，风向为无持续风向。

<!-- END_DOC_95 -->

---


<!-- START_DOC_96: best-practices.md (URL: https://dev.qweather.com/docs/best-practices) -->
Title: 最佳实践

URL Source: https://dev.qweather.com/docs/best-practices

Markdown Content:
[和风天气开发者服务](https://dev.qweather.com/)

[控制台](https://id.qweather.com/#/login?redirect=https://console.qweather.com)

*   [首页](https://dev.qweather.com/)
*   [文档](https://dev.qweather.com/docs/)
*   [图标](https://icons.qweather.com/)
*   [展示](https://dev.qweather.com/showcase/)
*   [帮助](https://dev.qweather.com/help/)
*   [价格](https://dev.qweather.com/price/)
*   Search K 

*   [文档](https://dev.qweather.com/docs/)
*   >
*   [最佳实践](https://dev.qweather.com/docs/best-practices/)

#### [开始使用](https://dev.qweather.com/docs/start/)

*   [特性](https://dev.qweather.com/docs/features/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [帐号管理](https://dev.qweather.com/docs/account/)

#### [开发文档](https://dev.qweather.com/docs/api/)

*   [GeoAPI](https://dev.qweather.com/docs/api/geoapi/)
*   [天气预报](https://dev.qweather.com/docs/api/weather/)
*   [分钟预报](https://dev.qweather.com/docs/api/minutely/)
*   [预警](https://dev.qweather.com/docs/api/warning/)
*   [天气指数](https://dev.qweather.com/docs/api/indices/)
*   [空气质量](https://dev.qweather.com/docs/api/air-quality/)
*   [时光机](https://dev.qweather.com/docs/api/time-machine/)
*   [热带气旋（台风）](https://dev.qweather.com/docs/api/tropical-cyclone/)
*   [海洋数据](https://dev.qweather.com/docs/api/ocean/)
*   [太阳辐射](https://dev.qweather.com/docs/api/solar-radiation/)
*   [天文](https://dev.qweather.com/docs/api/astronomy/)
*   [控制台API](https://dev.qweather.com/docs/api/console/)

#### [实用资料](https://dev.qweather.com/docs/resource/)

*   [专用词汇表](https://dev.qweather.com/docs/resource/glossary/)
*   [错误码](https://dev.qweather.com/docs/resource/error-code/)
*   [多语言](https://dev.qweather.com/docs/resource/language/)
*   [常见城市列表](https://dev.qweather.com/docs/resource/location-list/)
*   [元数据](https://dev.qweather.com/docs/resource/metadata/)
*   [单位](https://dev.qweather.com/docs/resource/unit/)

#### [最佳实践](https://dev.qweather.com/docs/best-practices/)

*   [不要假设](https://dev.qweather.com/docs/best-practices/no-assumptions/)
*   [优化请求](https://dev.qweather.com/docs/best-practices/optimize-requests/)
*   [处理Gzip](https://dev.qweather.com/docs/best-practices/gzip/)
*   [缓存你的数据](https://dev.qweather.com/docs/best-practices/cache/)
*   [安全指南](https://dev.qweather.com/docs/best-practices/security-guidelines/)

#### [财务与费用](https://dev.qweather.com/docs/finance/)

*   [计费方式和支付](https://dev.qweather.com/docs/finance/billing-and-payment/)
*   [按量计费定价](https://dev.qweather.com/docs/finance/pricing/)
*   [节省计划](https://dev.qweather.com/docs/finance/savings-plans/)
*   [增值税发票](https://dev.qweather.com/docs/finance/vat-invoice/)

#### [条款](https://dev.qweather.com/docs/terms/)

*   [使用限制](https://dev.qweather.com/docs/terms/restriction/)
*   [注明来源](https://dev.qweather.com/docs/terms/attribution/)
*   [服务条款](https://dev.qweather.com/docs/terms/tos/)

# 最佳实践

本文档介绍了一些使用我们服务的常见做法和经验，以便你能够快速的、稳定的获取你需要的数据。

[不要假设](https://dev.qweather.com/docs/best-practices/no-assumptions/)
不能假设天气数据始终完整或固定，应该让程序正确适配字段、枚举值和空值的变化。

[优化请求](https://dev.qweather.com/docs/best-practices/optimize-requests/)
了解如何构建合法且安全的 API 请求、正确处理错误，并减少不必要的数据请求。

[处理Gzip](https://dev.qweather.com/docs/best-practices/gzip/)
和风天气 API 强制使用 Gzip 压缩机制，这将减少开发者程序的网络流量，加快请求。

[缓存你的数据](https://dev.qweather.com/docs/best-practices/cache/)
了解如何为不同天气数据设置合理且弹性的缓存策略，以及推荐的缓存时间和使用限制。

[安全指南](https://dev.qweather.com/docs/best-practices/security-guidelines/)
和风天气提供了多种安全方式保护你的 API 请求和敏感信息。

### [和风天气开发者服务](https://dev.qweather.com/)

[联系](https://www.qweather.com/contact/)• [博客](https://blog.qweather.com/)• [GitHub](https://github.com/qwd)

Language 

### 文档

*   [开始使用](https://dev.qweather.com/docs/start/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [开发文档](https://dev.qweather.com/docs/api/)
*   [财务与费用](https://dev.qweather.com/docs/finance/)
*   [最佳实践](https://dev.qweather.com/docs/best-practices/)
*   [实用资料](https://dev.qweather.com/docs/resource/)

### 特性

*   [服务和数据](https://dev.qweather.com/docs/features/service-and-data/)
*   [全球部署](https://dev.qweather.com/docs/features/global-deployment/)
*   [性能](https://dev.qweather.com/docs/features/performance/)

### 更多

*   [天气预报](https://www.qweather.com/)
*   [天气地图](https://map.qweather.com/)
*   [下载APP](https://www.qweather.com/app/)

*   © 2026 [QWeather](https://www.qweather.com/en/)
*   [许可协议](https://www.qweather.com/terms/developers-eula/)
*   [隐私政策](https://www.qweather.com/terms/privacy)
*   [免责声明](https://www.qweather.com/terms/disclaimer/)

[京ICP备15048401号-11](https://beian.miit.gov.cn/)

<!-- END_DOC_96 -->

---


<!-- START_DOC_97: ios-sdk_weather.md (URL: https://dev.qweather.com/docs/ios-sdk/weather) -->
Title: iOS SDK 文档已迁移

URL Source: https://dev.qweather.com/docs/ios-sdk/weather

Markdown Content:
[和风天气开发者服务](https://dev.qweather.com/)

[控制台](https://id.qweather.com/#/login?redirect=https://console.qweather.com)

*   [首页](https://dev.qweather.com/)
*   [文档](https://dev.qweather.com/docs/)
*   [图标](https://icons.qweather.com/)
*   [展示](https://dev.qweather.com/showcase/)
*   [帮助](https://dev.qweather.com/help/)
*   [价格](https://dev.qweather.com/price/)
*   Search K 

# iOS SDK 文档已迁移

iOS SDK 文档已从开发者网站迁移至 GitHub 仓库。请访问以下仓库获取最新文档、示例代码、版本和问题追踪。

[➡️ iOS SDK 仓库](https://github.com/qwd/qweather-ios-sdk)

### [和风天气开发者服务](https://dev.qweather.com/)

[联系](https://www.qweather.com/contact/)• [博客](https://blog.qweather.com/)• [GitHub](https://github.com/qwd)

Language 

### 文档

*   [开始使用](https://dev.qweather.com/docs/start/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [开发文档](https://dev.qweather.com/docs/api/)
*   [财务与费用](https://dev.qweather.com/docs/finance/)
*   [最佳实践](https://dev.qweather.com/docs/best-practices/)
*   [实用资料](https://dev.qweather.com/docs/resource/)

### 特性

*   [服务和数据](https://dev.qweather.com/docs/features/service-and-data/)
*   [全球部署](https://dev.qweather.com/docs/features/global-deployment/)
*   [性能](https://dev.qweather.com/docs/features/performance/)

### 更多

*   [天气预报](https://www.qweather.com/)
*   [天气地图](https://map.qweather.com/)
*   [下载APP](https://www.qweather.com/app/)

*   © 2026 [QWeather](https://www.qweather.com/en/)
*   [许可协议](https://www.qweather.com/terms/developers-eula/)
*   [隐私政策](https://www.qweather.com/terms/privacy)
*   [免责声明](https://www.qweather.com/terms/disclaimer/)

[京ICP备15048401号-11](https://beian.miit.gov.cn/)

<!-- END_DOC_97 -->

---


<!-- START_DOC_98: ios-sdk_ocean_ios-tide.md (URL: https://dev.qweather.com/docs/ios-sdk/ocean/ios-tide) -->
Title: iOS SDK 文档已迁移

URL Source: https://dev.qweather.com/docs/ios-sdk/ocean/ios-tide

Markdown Content:
[和风天气开发者服务](https://dev.qweather.com/)

[控制台](https://id.qweather.com/#/login?redirect=https://console.qweather.com)

*   [首页](https://dev.qweather.com/)
*   [文档](https://dev.qweather.com/docs/)
*   [图标](https://icons.qweather.com/)
*   [展示](https://dev.qweather.com/showcase/)
*   [帮助](https://dev.qweather.com/help/)
*   [价格](https://dev.qweather.com/price/)
*   Search K 

# iOS SDK 文档已迁移

iOS SDK 文档已从开发者网站迁移至 GitHub 仓库。请访问以下仓库获取最新文档、示例代码、版本和问题追踪。

[➡️ iOS SDK 仓库](https://github.com/qwd/qweather-ios-sdk)

### [和风天气开发者服务](https://dev.qweather.com/)

[联系](https://www.qweather.com/contact/)• [博客](https://blog.qweather.com/)• [GitHub](https://github.com/qwd)

Language 

### 文档

*   [开始使用](https://dev.qweather.com/docs/start/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [开发文档](https://dev.qweather.com/docs/api/)
*   [财务与费用](https://dev.qweather.com/docs/finance/)
*   [最佳实践](https://dev.qweather.com/docs/best-practices/)
*   [实用资料](https://dev.qweather.com/docs/resource/)

### 特性

*   [服务和数据](https://dev.qweather.com/docs/features/service-and-data/)
*   [全球部署](https://dev.qweather.com/docs/features/global-deployment/)
*   [性能](https://dev.qweather.com/docs/features/performance/)

### 更多

*   [天气预报](https://www.qweather.com/)
*   [天气地图](https://map.qweather.com/)
*   [下载APP](https://www.qweather.com/app/)

*   © 2026 [QWeather](https://www.qweather.com/en/)
*   [许可协议](https://www.qweather.com/terms/developers-eula/)
*   [隐私政策](https://www.qweather.com/terms/privacy)
*   [免责声明](https://www.qweather.com/terms/disclaimer/)

[京ICP备15048401号-11](https://beian.miit.gov.cn/)

<!-- END_DOC_98 -->

---


<!-- START_DOC_99: android-sdk_air-quality.md (URL: https://dev.qweather.com/docs/android-sdk/air-quality) -->
Title: Android SDK 文档已迁移

URL Source: https://dev.qweather.com/docs/android-sdk/air-quality

Markdown Content:
[和风天气开发者服务](https://dev.qweather.com/)

[控制台](https://id.qweather.com/#/login?redirect=https://console.qweather.com)

*   [首页](https://dev.qweather.com/)
*   [文档](https://dev.qweather.com/docs/)
*   [图标](https://icons.qweather.com/)
*   [展示](https://dev.qweather.com/showcase/)
*   [帮助](https://dev.qweather.com/help/)
*   [价格](https://dev.qweather.com/price/)
*   Search K 

# Android SDK 文档已迁移

Android SDK 文档已从开发者网站迁移至 GitHub 仓库。请访问以下仓库获取最新文档、示例代码、版本和问题追踪。

[➡️ Android SDK 仓库](https://github.com/qwd/qweather-android-sdk)

### [和风天气开发者服务](https://dev.qweather.com/)

[联系](https://www.qweather.com/contact/)• [博客](https://blog.qweather.com/)• [GitHub](https://github.com/qwd)

Language 

### 文档

*   [开始使用](https://dev.qweather.com/docs/start/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [开发文档](https://dev.qweather.com/docs/api/)
*   [财务与费用](https://dev.qweather.com/docs/finance/)
*   [最佳实践](https://dev.qweather.com/docs/best-practices/)
*   [实用资料](https://dev.qweather.com/docs/resource/)

### 特性

*   [服务和数据](https://dev.qweather.com/docs/features/service-and-data/)
*   [全球部署](https://dev.qweather.com/docs/features/global-deployment/)
*   [性能](https://dev.qweather.com/docs/features/performance/)

### 更多

*   [天气预报](https://www.qweather.com/)
*   [天气地图](https://map.qweather.com/)
*   [下载APP](https://www.qweather.com/app/)

*   © 2026 [QWeather](https://www.qweather.com/en/)
*   [许可协议](https://www.qweather.com/terms/developers-eula/)
*   [隐私政策](https://www.qweather.com/terms/privacy)
*   [免责声明](https://www.qweather.com/terms/disclaimer/)

[京ICP备15048401号-11](https://beian.miit.gov.cn/)

<!-- END_DOC_99 -->

---


<!-- START_DOC_100: android-sdk_tropical-cyclone.md (URL: https://dev.qweather.com/docs/android-sdk/tropical-cyclone) -->
Title: Android SDK 文档已迁移

URL Source: https://dev.qweather.com/docs/android-sdk/tropical-cyclone

Markdown Content:
[和风天气开发者服务](https://dev.qweather.com/)

[控制台](https://id.qweather.com/#/login?redirect=https://console.qweather.com)

*   [首页](https://dev.qweather.com/)
*   [文档](https://dev.qweather.com/docs/)
*   [图标](https://icons.qweather.com/)
*   [展示](https://dev.qweather.com/showcase/)
*   [帮助](https://dev.qweather.com/help/)
*   [价格](https://dev.qweather.com/price/)
*   Search K 

# Android SDK 文档已迁移

Android SDK 文档已从开发者网站迁移至 GitHub 仓库。请访问以下仓库获取最新文档、示例代码、版本和问题追踪。

[➡️ Android SDK 仓库](https://github.com/qwd/qweather-android-sdk)

### [和风天气开发者服务](https://dev.qweather.com/)

[联系](https://www.qweather.com/contact/)• [博客](https://blog.qweather.com/)• [GitHub](https://github.com/qwd)

Language 

### 文档

*   [开始使用](https://dev.qweather.com/docs/start/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [开发文档](https://dev.qweather.com/docs/api/)
*   [财务与费用](https://dev.qweather.com/docs/finance/)
*   [最佳实践](https://dev.qweather.com/docs/best-practices/)
*   [实用资料](https://dev.qweather.com/docs/resource/)

### 特性

*   [服务和数据](https://dev.qweather.com/docs/features/service-and-data/)
*   [全球部署](https://dev.qweather.com/docs/features/global-deployment/)
*   [性能](https://dev.qweather.com/docs/features/performance/)

### 更多

*   [天气预报](https://www.qweather.com/)
*   [天气地图](https://map.qweather.com/)
*   [下载APP](https://www.qweather.com/app/)

*   © 2026 [QWeather](https://www.qweather.com/en/)
*   [许可协议](https://www.qweather.com/terms/developers-eula/)
*   [隐私政策](https://www.qweather.com/terms/privacy)
*   [免责声明](https://www.qweather.com/terms/disclaimer/)

[京ICP备15048401号-11](https://beian.miit.gov.cn/)

<!-- END_DOC_100 -->

---


<!-- START_DOC_101: android-sdk_weather_android-grid-weather-now.md (URL: https://dev.qweather.com/docs/android-sdk/weather/android-grid-weather-now) -->
Title: Android SDK 文档已迁移

URL Source: https://dev.qweather.com/docs/android-sdk/weather/android-grid-weather-now

Markdown Content:
[和风天气开发者服务](https://dev.qweather.com/)

[控制台](https://id.qweather.com/#/login?redirect=https://console.qweather.com)

*   [首页](https://dev.qweather.com/)
*   [文档](https://dev.qweather.com/docs/)
*   [图标](https://icons.qweather.com/)
*   [展示](https://dev.qweather.com/showcase/)
*   [帮助](https://dev.qweather.com/help/)
*   [价格](https://dev.qweather.com/price/)
*   Search K 

# Android SDK 文档已迁移

Android SDK 文档已从开发者网站迁移至 GitHub 仓库。请访问以下仓库获取最新文档、示例代码、版本和问题追踪。

[➡️ Android SDK 仓库](https://github.com/qwd/qweather-android-sdk)

### [和风天气开发者服务](https://dev.qweather.com/)

[联系](https://www.qweather.com/contact/)• [博客](https://blog.qweather.com/)• [GitHub](https://github.com/qwd)

Language 

### 文档

*   [开始使用](https://dev.qweather.com/docs/start/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [开发文档](https://dev.qweather.com/docs/api/)
*   [财务与费用](https://dev.qweather.com/docs/finance/)
*   [最佳实践](https://dev.qweather.com/docs/best-practices/)
*   [实用资料](https://dev.qweather.com/docs/resource/)

### 特性

*   [服务和数据](https://dev.qweather.com/docs/features/service-and-data/)
*   [全球部署](https://dev.qweather.com/docs/features/global-deployment/)
*   [性能](https://dev.qweather.com/docs/features/performance/)

### 更多

*   [天气预报](https://www.qweather.com/)
*   [天气地图](https://map.qweather.com/)
*   [下载APP](https://www.qweather.com/app/)

*   © 2026 [QWeather](https://www.qweather.com/en/)
*   [许可协议](https://www.qweather.com/terms/developers-eula/)
*   [隐私政策](https://www.qweather.com/terms/privacy)
*   [免责声明](https://www.qweather.com/terms/disclaimer/)

[京ICP备15048401号-11](https://beian.miit.gov.cn/)

<!-- END_DOC_101 -->

---


<!-- START_DOC_102: android-sdk_air-quality_android-air-daily-forecast.md (URL: https://dev.qweather.com/docs/android-sdk/air-quality/android-air-daily-forecast) -->
Title: Android SDK 文档已迁移

URL Source: https://dev.qweather.com/docs/android-sdk/air-quality/android-air-daily-forecast

Published Time: Thu, 27 Aug 2026 02:08:36 GMT

Markdown Content:
[和风天气开发者服务](https://dev.qweather.com/)

[控制台](https://id.qweather.com/#/login?redirect=https://console.qweather.com)

*   [首页](https://dev.qweather.com/)
*   [文档](https://dev.qweather.com/docs/)
*   [图标](https://icons.qweather.com/)
*   [展示](https://dev.qweather.com/showcase/)
*   [帮助](https://dev.qweather.com/help/)
*   [价格](https://dev.qweather.com/price/)
*   Search K 

# Android SDK 文档已迁移

Android SDK 文档已从开发者网站迁移至 GitHub 仓库。请访问以下仓库获取最新文档、示例代码、版本和问题追踪。

[➡️ Android SDK 仓库](https://github.com/qwd/qweather-android-sdk)

### [和风天气开发者服务](https://dev.qweather.com/)

[联系](https://www.qweather.com/contact/)• [博客](https://blog.qweather.com/)• [GitHub](https://github.com/qwd)

Language 

### 文档

*   [开始使用](https://dev.qweather.com/docs/start/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [开发文档](https://dev.qweather.com/docs/api/)
*   [财务与费用](https://dev.qweather.com/docs/finance/)
*   [最佳实践](https://dev.qweather.com/docs/best-practices/)
*   [实用资料](https://dev.qweather.com/docs/resource/)

### 特性

*   [服务和数据](https://dev.qweather.com/docs/features/service-and-data/)
*   [全球部署](https://dev.qweather.com/docs/features/global-deployment/)
*   [性能](https://dev.qweather.com/docs/features/performance/)

### 更多

*   [天气预报](https://www.qweather.com/)
*   [天气地图](https://map.qweather.com/)
*   [下载APP](https://www.qweather.com/app/)

*   © 2026 [QWeather](https://www.qweather.com/en/)
*   [许可协议](https://www.qweather.com/terms/developers-eula/)
*   [隐私政策](https://www.qweather.com/terms/privacy)
*   [免责声明](https://www.qweather.com/terms/disclaimer/)

[京ICP备15048401号-11](https://beian.miit.gov.cn/)

<!-- END_DOC_102 -->

---


<!-- START_DOC_103: ios-sdk_air-quality_ios-air-hourly-forecast.md (URL: https://dev.qweather.com/docs/ios-sdk/air-quality/ios-air-hourly-forecast) -->
Title: iOS SDK 文档已迁移

URL Source: https://dev.qweather.com/docs/ios-sdk/air-quality/ios-air-hourly-forecast

Markdown Content:
[和风天气开发者服务](https://dev.qweather.com/)

[控制台](https://id.qweather.com/#/login?redirect=https://console.qweather.com)

*   [首页](https://dev.qweather.com/)
*   [文档](https://dev.qweather.com/docs/)
*   [图标](https://icons.qweather.com/)
*   [展示](https://dev.qweather.com/showcase/)
*   [帮助](https://dev.qweather.com/help/)
*   [价格](https://dev.qweather.com/price/)
*   Search K 

# iOS SDK 文档已迁移

iOS SDK 文档已从开发者网站迁移至 GitHub 仓库。请访问以下仓库获取最新文档、示例代码、版本和问题追踪。

[➡️ iOS SDK 仓库](https://github.com/qwd/qweather-ios-sdk)

### [和风天气开发者服务](https://dev.qweather.com/)

[联系](https://www.qweather.com/contact/)• [博客](https://blog.qweather.com/)• [GitHub](https://github.com/qwd)

Language 

### 文档

*   [开始使用](https://dev.qweather.com/docs/start/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [开发文档](https://dev.qweather.com/docs/api/)
*   [财务与费用](https://dev.qweather.com/docs/finance/)
*   [最佳实践](https://dev.qweather.com/docs/best-practices/)
*   [实用资料](https://dev.qweather.com/docs/resource/)

### 特性

*   [服务和数据](https://dev.qweather.com/docs/features/service-and-data/)
*   [全球部署](https://dev.qweather.com/docs/features/global-deployment/)
*   [性能](https://dev.qweather.com/docs/features/performance/)

### 更多

*   [天气预报](https://www.qweather.com/)
*   [天气地图](https://map.qweather.com/)
*   [下载APP](https://www.qweather.com/app/)

*   © 2026 [QWeather](https://www.qweather.com/en/)
*   [许可协议](https://www.qweather.com/terms/developers-eula/)
*   [隐私政策](https://www.qweather.com/terms/privacy)
*   [免责声明](https://www.qweather.com/terms/disclaimer/)

[京ICP备15048401号-11](https://beian.miit.gov.cn/)

<!-- END_DOC_103 -->

---


<!-- START_DOC_104: android-sdk_air-quality_android-air-hourly-forecast.md (URL: https://dev.qweather.com/docs/android-sdk/air-quality/android-air-hourly-forecast) -->
Title: Android SDK 文档已迁移

URL Source: https://dev.qweather.com/docs/android-sdk/air-quality/android-air-hourly-forecast

Markdown Content:
[和风天气开发者服务](https://dev.qweather.com/)

[控制台](https://id.qweather.com/#/login?redirect=https://console.qweather.com)

*   [首页](https://dev.qweather.com/)
*   [文档](https://dev.qweather.com/docs/)
*   [图标](https://icons.qweather.com/)
*   [展示](https://dev.qweather.com/showcase/)
*   [帮助](https://dev.qweather.com/help/)
*   [价格](https://dev.qweather.com/price/)
*   Search K 

# Android SDK 文档已迁移

Android SDK 文档已从开发者网站迁移至 GitHub 仓库。请访问以下仓库获取最新文档、示例代码、版本和问题追踪。

[➡️ Android SDK 仓库](https://github.com/qwd/qweather-android-sdk)

### [和风天气开发者服务](https://dev.qweather.com/)

[联系](https://www.qweather.com/contact/)• [博客](https://blog.qweather.com/)• [GitHub](https://github.com/qwd)

Language 

### 文档

*   [开始使用](https://dev.qweather.com/docs/start/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [开发文档](https://dev.qweather.com/docs/api/)
*   [财务与费用](https://dev.qweather.com/docs/finance/)
*   [最佳实践](https://dev.qweather.com/docs/best-practices/)
*   [实用资料](https://dev.qweather.com/docs/resource/)

### 特性

*   [服务和数据](https://dev.qweather.com/docs/features/service-and-data/)
*   [全球部署](https://dev.qweather.com/docs/features/global-deployment/)
*   [性能](https://dev.qweather.com/docs/features/performance/)

### 更多

*   [天气预报](https://www.qweather.com/)
*   [天气地图](https://map.qweather.com/)
*   [下载APP](https://www.qweather.com/app/)

*   © 2026 [QWeather](https://www.qweather.com/en/)
*   [许可协议](https://www.qweather.com/terms/developers-eula/)
*   [隐私政策](https://www.qweather.com/terms/privacy)
*   [免责声明](https://www.qweather.com/terms/disclaimer/)

[京ICP备15048401号-11](https://beian.miit.gov.cn/)

<!-- END_DOC_104 -->

---


<!-- START_DOC_105: android-sdk_time-machine_android-time-machine-weather.md (URL: https://dev.qweather.com/docs/android-sdk/time-machine/android-time-machine-weather) -->
Title: Android SDK 文档已迁移

URL Source: https://dev.qweather.com/docs/android-sdk/time-machine/android-time-machine-weather

Markdown Content:
[和风天气开发者服务](https://dev.qweather.com/)

[控制台](https://id.qweather.com/#/login?redirect=https://console.qweather.com)

*   [首页](https://dev.qweather.com/)
*   [文档](https://dev.qweather.com/docs/)
*   [图标](https://icons.qweather.com/)
*   [展示](https://dev.qweather.com/showcase/)
*   [帮助](https://dev.qweather.com/help/)
*   [价格](https://dev.qweather.com/price/)
*   Search K 

# Android SDK 文档已迁移

Android SDK 文档已从开发者网站迁移至 GitHub 仓库。请访问以下仓库获取最新文档、示例代码、版本和问题追踪。

[➡️ Android SDK 仓库](https://github.com/qwd/qweather-android-sdk)

### [和风天气开发者服务](https://dev.qweather.com/)

[联系](https://www.qweather.com/contact/)• [博客](https://blog.qweather.com/)• [GitHub](https://github.com/qwd)

Language 

### 文档

*   [开始使用](https://dev.qweather.com/docs/start/)
*   [开发配置](https://dev.qweather.com/docs/configuration/)
*   [开发文档](https://dev.qweather.com/docs/api/)
*   [财务与费用](https://dev.qweather.com/docs/finance/)
*   [最佳实践](https://dev.qweather.com/docs/best-practices/)
*   [实用资料](https://dev.qweather.com/docs/resource/)

### 特性

*   [服务和数据](https://dev.qweather.com/docs/features/service-and-data/)
*   [全球部署](https://dev.qweather.com/docs/features/global-deployment/)
*   [性能](https://dev.qweather.com/docs/features/performance/)

### 更多

*   [天气预报](https://www.qweather.com/)
*   [天气地图](https://map.qweather.com/)
*   [下载APP](https://www.qweather.com/app/)

*   © 2026 [QWeather](https://www.qweather.com/en/)
*   [许可协议](https://www.qweather.com/terms/developers-eula/)
*   [隐私政策](https://www.qweather.com/terms/privacy)
*   [免责声明](https://www.qweather.com/terms/disclaimer/)

[京ICP备15048401号-11](https://beian.miit.gov.cn/)

<!-- END_DOC_105 -->

---
