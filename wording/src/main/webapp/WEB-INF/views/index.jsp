<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.0.0/css/bootstrap.min.css" />
    <title>Product Wording Generator</title>
</head>
<body>
    <div class="container" style="margin-top: 80px; text-align: center;">
        <h2>Product Wording Generator</h2>
        <p class="text-muted" style="margin-bottom: 40px;">이커머스 상품명 정규식 패턴 생성 관리 툴</p>
        <div>
            <a href="${pageContext.request.contextPath}/goods-wording-insert" class="btn btn-primary btn-lg" style="margin-right: 20px;">
                정제어 등록
            </a>
            <a href="${pageContext.request.contextPath}/goods-wording-update" class="btn btn-default btn-lg">
                정제어 수정
            </a>
        </div>
    </div>
</body>
</html>
