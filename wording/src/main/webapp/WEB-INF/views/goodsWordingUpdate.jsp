<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=EUC-KR">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.0.0/css/bootstrap.min.css" />
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
<title>Insert title here</title>
</head>
<body>
<h3 class="container"> 정제어 UPDATE 리스트 </h3>
	<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
	<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
	<div class="container" id="wordList">
		<form action ="${pageContext.request.contextPath}/allUpdateWording" method="post" id="allUpdateWording" >
			<div id="hidden"></div>
			<div class="panel-body" id="wordBody"></div>
			<input type="submit" value="ALL+UPDATE" class="text-center btn btn-warning" />
		</form>
		<div class="text-center listBtn" id="listBtn"></div>
	</div>
	<script>
	$(function(){
		list("goodsWordingUpdate", 0);
		
		$(document).on("click", "#wordBody a", function(){
			var goodsName = $(this).attr("name");
			var id = $(this).attr("id");
			
			var brandYn = $("input:checkbox[name='brand"+id+"']").is(":checked");
			var goodsYn = $("input:checkbox[name='goods"+id+"']").is(":checked");
			
			$.ajax({
				url : "${pageContext.request.contextPath}/wording", type : "get", dataType : "text",
				data : { "goodsName" : goodsName, "brandYn" : brandYn, "goodsYn": goodsYn, "id" : id },
				success : function(data) {
					$("#wording"+id).html("");
					$("#btn"+id).html("");
					var btn = $("<input type='button' value='UPDATE' name='"+id+"' class='btn btn-default' />");
					$("#wording"+id).val(data);
					$("#btn"+id).append(btn);
				},
				error : function(xhr, textStatus, errorThrown) { alert(textStatus+" (HTTP-"+xhr.status+"/"+errorThrown+")"); }
			});
			return false;
		});
		
		$(document).on("click", "#wordBody input[type=button]", function(){
			var id = $(this).attr("name");
			var word1 = $("#wording"+id).val();
			var word2 = $("#word"+id).val();
			
			$.ajax({
				url : "${pageContext.request.contextPath}/updateWording", type : "get", dataType : "text",
				data : { "word1" : word1, "word2" : word2 , "id" : id },
				success : function(data) { alert(data); },
				error : function(xhr, textStatus, errorThrown) { alert(textStatus+" (HTTP-"+xhr.status+"/"+errorThrown+")"); }
			});
			return false;
		});
	});
	
	$(document).on("click", "#listBtn .pagination a", function(){
		var str = $(this).attr("href");
		var pstartno = parseInt(str.split("=")[1]);
		list("newMain2", pstartno);
		return false;
	});
	
	function list(page, pstartno) {
		$.ajax({
			url : "${pageContext.request.contextPath}/list2",
			type : "get",
			data : { "pstartno" : pstartno, "page" : page },
			success : function(data) {
				$("#wordBody").empty();
				$("#hidden").empty();
				$("#wordList .listBtn").empty();
				
				var onepagelimit = data[data.length-1].onepagelimit;
				var pagetotal = data[data.length-1].pagetotal;
				var currentpage = data[data.length-1].currentpage;
				var startpage = data[data.length-1].startpage; 
				var endpage = data[data.length-1].endpage;
				var bottomlimit = data[data.length-1].bottomlimit;
				var listcount = data[data.length-1].listcount;
				var search = data[data.length-1].search;
				var prev = "";
				var next = "";
				
				var startNo = $("<input type='hidden' value='"+data[0].id+"' name='startNo' >");
				var endNo = $("<input type='hidden' value='"+data[data.length-2].id+"' name='endNo' >");
				$("#hidden").append(startNo).append(endNo);
				
				for(var i=0; i<data.length-1; i++) {
					var goodsName = data[i].goodsName;
					var id = data[i].id;
					var wording1 = data[i].word1;
					var wording2 = data[i].word2;
					
					var p = $("<p>");
					var title = "<strong>"+id+" : "+goodsName+"</strong>";
					var content = "<a href='${pageContext.request.contextPath}/wording' class='btn btn-default' name='"+goodsName+"' id='"+id+"' >정제</a>";
					var brandChk = "<label>브랜드</label><input type='checkbox' name='brand"+id+"' />";
					var goodsChk = "<label>상품명</label><input type='checkbox' name='goods"+id+"' class='chkbox' />";
					
					p.append(title).append(content).append(brandChk).append(goodsChk);
					
					var word1 = "<label><strong>WORD1 : </strong></label><input type='text' id='wording"+id+"' name='wording"+id+"' value='"+wording1+"' class='form-control'/>";
					var word2 = "<label><strong>WORD2 : </strong></label><input type='text' id='word"+id+"' name='word"+id+"' value='"+wording2+"' class='form-control'/>";
					
					var btn = $("<p id='btn"+id+"' class='"+id+"' >");
					
					$("#wordList #wordBody").append(p).append(word1).append(word2).append(btn);
				}
				
				var ul = $("<ul class='pagination'>");
				if(startpage >= bottomlimit) {
					prev = $("<li>").html("<a href='${pageContext.request.contextPath}/list2?pstartno="+((startpage-2)*onepagelimit)+"' class='prev'>[이전]</a>");
					ul.append(prev);
				}
				
				for(var i= startpage; i<=endpage; i++) {
					var li = "";
					if(currentpage == i) {
						li = $("<li class='active'>").html("<a href='${pageContext.request.contextPath}/list2?pstartno="+(i-1)*onepagelimit+"' id='button' class='btn btn-default'>"+i+"</a>");
					} else {
						li = $("<li>").html("<a href='${pageContext.request.contextPath}/list2?pstartno="+(i-1)*onepagelimit+"' id='button' class='btn btn-default'>"+i+"</a>");
					}
					ul.append(li);
				}

				if(pagetotal> endpage ) {
					next = $("<li>").html("<a href='${pageContext.request.contextPath}/list2?pstartno="+(endpage*onepagelimit)+"' class='next'>[다음]</a>");
					ul.append(next);
				}
				$("#wordList .listBtn").append(ul);
				$("input:checkbox[class='chkbox']").prop('checked', true);
			}, error : function(xhr, textStatus, errorThrown) { $("#boardList tbody").html(textStatus+" (HTTP-"+xhr.status+"/"+errorThrown+")"); }
		});
	}
	</script>
</body>
</html>