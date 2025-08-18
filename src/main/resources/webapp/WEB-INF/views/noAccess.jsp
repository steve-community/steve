<%--

    SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
    Copyright (C) 2013-2024 SteVe Community Team
    All Rights Reserved.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.

--%>

<%@ include file="00-header.jsp" %>
<%@ include file="00-context.jsp" %>

<div class="content">
	<div>
		<section>
			<span>
				Access denied for the requested page! For further information ask your administrator.
    			<a class="tooltip" href="#"><img src="${ctxPath}/static/images/info.png" style="vertical-align:middle" alt="info_image">
            		<span>Some information and configuration pages are only accessible for administrators.</span>
        		</a>
			</span>
		</section>

    <a href="${ctxPath}/manager/home">Home</a>
	</div>
</div>
<%@ include file="00-footer.jsp" %>
