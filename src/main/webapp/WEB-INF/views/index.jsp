<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
  <%@ include file="layout/header.jspf" %>
  <body>
  <div id="posts_list">
            <!-- 목록 출력 영역 -->
            <table id="table" class="table table-horizontal table-bordered">
                <thead id="thead" class="thead-strong">
                <tr>
                    <th>게시글번호</th>
                    <th>제목</th>
                    <th>작성자</th>
                    <th>최종수정일</th>
                    <th>조회수</th>
                </tr>
                </thead>
                <tbody id="tbody">
                {{#posts}}
                    <tr>
                        <td><a href="/posts/read/{{id}}">{{id}}</a></td>
                        <td><a href="/posts/read/{{id}}">{{title}}</a></td>
                        <td>{{writer}}</td>
                        <td>{{modifiedDate}}</td>
                        <td>{{view}}</td>
                    </tr>
                {{/posts}}
                </tbody>
            </table>
            {{! Page }}
            {{! Page }}
            <div class="pagination justify-content-center">
                {{#hasPrev}}
                    <a href="?page={{previous}}" role="button" class="btn btn-lg bi bi-caret-left-square-fill"></a>
                {{/hasPrev}}
                {{^hasPrev}}
                    <a href="?page={{previous}}" role="button" class="btn btn-lg bi bi-caret-left-square-fill disabled"></a>
                {{/hasPrev}}
                {{#hasNext}}
                    <a href="?page={{next}}" role="button" class="btn btn-lg bi bi-caret-right-square-fill"></a>
                {{/hasNext}}
                {{^hasNext}}
                    <a href="?page={{next}}" role="button" class="btn btn-lg bi bi-caret-right-square-fill disabled"></a>
                {{/hasNext}}
            </div>
            {{#user}}
            <div style="text-align:right">
                <a href="/posts/write" role="button" class="btn btn-primary bi bi-pencil-fill"> 글쓰기</a>
            </div>
            {{/user}}
            {{^user}}
                <div style="text-align:right">
                    <a role="button" class="btn btn-primary">비회원은 글을 작성할 수 없습니다</a>
                </div>
            {{/user}}
        </div>
  <%@ include file="layout/footer.jspf" %>
  </body>
</html>