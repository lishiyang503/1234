# 添加护理级别脚本

# 添加高级护理
Invoke-RestMethod -Uri http://localhost:8990/nursing/levels -Method POST -ContentType 'application/json' -Body '{"name":"高级护理","level":"高级护理","description":"中级护理+康复训练+专业医疗护理","baseCost":8800}'

# 添加VIP护理
Invoke-RestMethod -Uri http://localhost:8990/nursing/levels -Method POST -ContentType 'application/json' -Body '{"name":"VIP护理","level":"VIP","description":"包含所有护理项目，专属服务","baseCost":10000}'

# 添加SVIP护理
Invoke-RestMethod -Uri http://localhost:8990/nursing/levels -Method POST -ContentType 'application/json' -Body '{"name":"SVIP护理","level":"SVIP","description":"私人医生，专属定制服务","baseCost":15000}'

# 验证结果
Invoke-RestMethod -Uri http://localhost:8990/nursing/nursing-level-options -UseBasicParsing