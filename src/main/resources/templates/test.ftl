<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8"/>
<title>Insert title here</title>
</head>
<body>
<!--  Hello <span th:text="*{name}"></span>! -->
<br />
<input type="button" value="POST Person" onclick="postPerson()"/>
<input type="button" value="PUT Person" onclick="putPerson()"/>
<input type="button" value="GET Person" onclick="getPerson()"/>
<input type="button" value="DELETE Person" onclick="deletePerson()"/>

<br />
<br />
<input type="button" value="POST Vehicle" onclick="postVehicle()"/>
<input type="button" value="PUT Vehicle" onclick="putVehicle()"/>
<input type="button" value="GET Vehicle" onclick="getVehicle()"/>
<input type="button" value="DELETE Vehicle" onclick="deleteVehicle()"/>

<br />
<br />
<input type="button" value="POST Thing" onclick="postThing()"/>
<input type="button" value="PUT Thing" onclick="putThing()"/>
<input type="button" value="GET Thing" onclick="getThing()"/>
<input type="button" value="DELETE Thing" onclick="deleteThing()"/>

<br />
<br />
<input type="button" value="POST Scene" onclick="postScene()"/>
<input type="button" value="PUT Scene" onclick="putScene()"/>
<input type="button" value="GET Scene" onclick="getScene()"/>
<input type="button" value="DELETE Scene" onclick="deleteScene()"/>

<br />
<br />
<input type="button" value="POST Clue" onclick="postClue()"/>
<input type="button" value="PUT Clue" onclick="putClue()"/>
<input type="button" value="GET Clue" onclick="getClue()"/>
<input type="button" value="DELETE Clue" onclick="deleteClue()"/>

<br />
<br />
<input type="button" value="POST Case" onclick="postCase()"/>
<input type="button" value="PUT Case" onclick="putCase()"/>
<input type="button" value="GET Case" onclick="getCase()"/>
<input type="button" value="DELETE Case" onclick="deleteCase()"/>

<br />
<br />
<input type="button" value="POST CaseClueRelation" onclick="postCaseClueRelation()"/>
<input type="button" value="PUT CaseClueRelation" onclick="putCaseClueRelation()"/>
<input type="button" value="GET CaseClueRelation" onclick="getCaseClueRelation()"/>
<input type="button" value="DELETE CaseClueRelation" onclick="deleteCaseClueRelation()"/>

<br />
<br />
<input type="button" value="POST ClueFileRelation" onclick="postClueFileRelation()"/>
<input type="button" value="PUT ClueFileRelation" onclick="putClueFileRelation()"/>
<input type="button" value="GET ClueFileRelation" onclick="getClueFileRelation()"/>
<input type="button" value="DELETE ClueFileRelation" onclick="deleteClueFileRelation()"/>

<br />
<br />
<input type="button" value="POST Video" onclick="postVideo()"/>
<input type="button" value="PUT Video" onclick="putVideo()"/>
<input type="button" value="GET Video" onclick="getVideo()"/>
<input type="button" value="DELETE Video" onclick="deleteVideo()"/>

<br />
<br />
<input type="button" value="POST Image" onclick="postImage()"/>
<input type="button" value="PUT Image" onclick="putImage()"/>
<input type="button" value="GET Image" onclick="getImage()"/>
<input type="button" value="DELETE Image" onclick="deleteImage()"/>

<br />
<br />
<input type="button" value="POST File" onclick="postFile()"/>
<input type="button" value="PUT File" onclick="putFile()"/>
<input type="button" value="GET File" onclick="getFile()"/>
<input type="button" value="DELETE File" onclick="deleteFile()"/>

<br />
<br />
<input type="button" value="POST Equipment" onclick="postEquipment()"/>
<input type="button" value="PUT Equipment" onclick="putEquipment()"/>
<input type="button" value="GET Equipment" onclick="getEquipment()"/>
<input type="button" value="DELETE Equipment" onclick="deleteEquipment()"/>

<script type="text/javascript" src="../js/jquery-1.8.3.min.js"></script>
<script type="text/javascript">
	function postPerson() {
		$.ajax({
	        url: '../persons',
	        type: "POST",
	        dataType: "json",
			contentType: "application/json; charset=utf-8",
	        data: JSON.stringify([{personID: "0201407241504456690902", sourceID: "abcdefg", clueID: "201605241439536414162"},
				{personID: "0201407241504456690903", sourceID: "abcdefg", clueID: "201605241439536414162"}]),
	        success: function (data) {
	            alert(JSON.stringify(data));
	        },
	        error: function () { alert('post fail'); }
	    });
	}
	
	function putPerson() {
		$.ajax({
	        url: '../persons',
	        type: "PUT",
	        dataType: "text",
			contentType: "application/json; charset=utf-8",
	        data: JSON.stringify([{personID: "0201407241504456690902", sourceID: "1234567", clueID: "201605241439536414162", corpseConditionCode: "溺亡1天"},
				{personID: "0201407241504456690903", sourceID: "1234567", clueID: "201605241439536414162"}]),
	        success: function (data) {
	            alert(data);
	        },
	        error: function () { alert('put fail'); }
	    });
	}
	
	function getPerson() {
		 $.ajax({
		        url: '../persons',
		        type: "GET",
		        dataType: "json",
		        data: {clueID: "201605241439536414162"},
		        success: function (data) {
		            alert(JSON.stringify(data));
		        },
		        error: function () { alert('get fail'); }
		    });
	}
	
	function deletePerson() {
		$.ajax({
	        url: '../persons',
	        type: "DELETE",
	        dataType: "text",
			contentType: "application/json; charset=utf-8",
	        data: JSON.stringify(["0201407241504456690902", "0201407241504456690903"]),
	        success: function (data) {
	            alert(data);
	        },
	        error: function () { alert('delete fail'); }
	    });
	}
	
	function postVehicle() {
		$.ajax({
	        url: '../vehicles',
	        type: "POST",
	        dataType: "text",	        
	        data: {motorVehicleID: "0201407241504456690902", sourceID: "abcdefg", passTime:"昨天"},
	        success: function (data) {
	            alert(data);
	        },
	        error: function () { alert('post fail'); }
	    });
	}
	
	function putVehicle() {
		$.ajax({
	        url: '../vehicles',
	        type: "PUT",
	        dataType: "text",	        
	        data: {motorVehicleID: "0201407241504456690902", sourceID: "1234567", passTime:"今天"},
	        success: function (data) {
	            alert(data);
	        },
	        error: function () { alert('put fail'); }
	    });
	}
	
	function getVehicle() {
		 $.ajax({
		        url: '../vehicles/0201407241504456690902',
		        type: "GET",
		        dataType: "json",	        
		        data: {},
		        success: function (data) {
		            alert(JSON.stringify(data));
		        },
		        error: function () { alert('get fail'); }
		    });
	}
	
	function deleteVehicle() {
		$.ajax({
	        url: '../vehicles',
	        type: "DELETE",
	        dataType: "text",	        
	        data: {motorVehicleID: "0201407241504456690902", sourceID: "1234567"},
	        success: function (data) {
	            alert(data);
	        },
	        error: function () { alert('delete fail'); }
	    });
	}
	
	function postThing() {
		$.ajax({
	        url: '../Things',
	        type: "POST",
	        dataType: "text",	        
	        data: {thingID: "0201407241504456690902", sourceID: "abcdefg", name: "手机"},
	        success: function (data) {
	            alert(data);
	        },
	        error: function () { alert('post fail'); }
	    });
	}
	
	function putThing() {
		$.ajax({
	        url: '../thingS',
	        type: "PUT",
	        dataType: "text",	        
	        data: {thingID: "0201407241504456690902", sourceID: "1234567", name: "iphone7"},
	        success: function (data) {
	            alert(data);
	        },
	        error: function () { alert('put fail'); }
	    });
	}
	
	function getThing() {
		 $.ajax({
		        url: '../thingS/0201407241504456690902',
		        type: "GET",
		        dataType: "json",	        
		        data: {},
		        success: function (data) {
		            alert(JSON.stringify(data));
		        },
		        error: function () { alert('get fail'); }
		    });
	}
	
	function deleteThing() {
		$.ajax({
	        url: '../THINGS',
	        type: "DELETE",
	        dataType: "text",	        
	        data: {thingID: "0201407241504456690902", sourceID: "1234567"},
	        success: function (data) {
	            alert(data);
	        },
	        error: function () { alert('delete fail'); }
	    });
	}
	
	function postScene() {
		$.ajax({
	        url: '../scenes',
	        type: "POST",
	        dataType: "text",	        
	        data: {sceneID: "0201407241504456690902", sourceID: "abcdefg", startTime: "2016/2/22 9:00"},
	        success: function (data) {
	            alert(data);
	        },
	        error: function () { alert('post fail'); }
	    });
	}
	
	function putScene() {
		$.ajax({
	        url: '../scenes',
	        type: "PUT",
	        dataType: "text",	        
	        data: {sceneID: "0201407241504456690902", sourceID: "1234567", startTime: "2015-3-1 0:00"},
	        success: function (data) {
	            alert(data);
	        },
	        error: function () { alert('put fail'); }
	    });
	}
	
	function getScene() {
		 $.ajax({
		        url: '../scenes/0201407241504456690902',
		        type: "GET",
		        dataType: "json",	        
		        data: {},
		        success: function (data) {
		            alert(JSON.stringify(data));
		        },
		        error: function () { alert('get fail'); }
		    });
	}
	
	function deleteScene() {
		$.ajax({
	        url: '../scenes',
	        type: "DELETE",
	        dataType: "text",	        
	        data: {sceneID: "0201407241504456690902", sourceID: "1234567"},
	        success: function (data) {
	            alert(data);
	        },
	        error: function () { alert('delete fail'); }
	    });
	}
	
	function postCase() {
		$.ajax({
	        url: '../cases',
	        type: "POST",
	        dataType: "text",	        
	        data: {caseID: "AL000526681025", caseName: "abcdefg", startTime: "2016/2/22 9:00", 
	        	caseAbstract: "杀人案", longtitude: 1000},
	        success: function (data) {
	            alert(data);
	        },
	        error: function () { alert('post fail'); }
	    });
	}
	
	function putCase() {
		$.ajax({
	        url: '../cases',
	        type: "PUT",
	        dataType: "text",	        
	        data: {caseID: "AL000526681025", caseName: "1234567", startTime: "2015/3/1 0:00", 
	        	caseAbstract: "强奸案", latitude: 9999},
	        success: function (data) {
	            alert(data);
	        },
	        error: function () { alert('put fail'); }
	    });
	}
	
	function getCase() {
		 $.ajax({
		        url: '../cases/AL000526681025',
		        type: "GET",
		        dataType: "json",	        
		        data: {},
		        success: function (data) {
		            alert(JSON.stringify(data));
		        },
		        error: function () { alert('get fail'); }
		    });
	}
	
	function deleteCase() {
		$.ajax({
	        url: '../cases',
	        type: "DELETE",
	        dataType: "text",	        
	        data: {caseID: "AL000526681025", caseName: "1234567"},
	        success: function (data) {
	            alert(data);
	        },
	        error: function () { alert('delete fail'); }
	    });
	}

	function postClue() {
		$.ajax({
			url: '../clues',
			type: "POST",
			dataType: "text",
			data: {clueID: "AL000526681025", clueName: "abcdefg", clueTime: "2016/2/22 9:00"},
			success: function (data) {
				alert(data);
			},
			error: function () { alert('post fail'); }
		});
	}

	function putClue() {
		$.ajax({
			url: '../clues',
			type: "PUT",
			dataType: "text",
			data: {clueID: "AL000526681025", clueName: "1234567", clueTime: "2015/3/1 0:00"},
			success: function (data) {
				alert(data);
			},
			error: function () { alert('put fail'); }
		});
	}

	function getClue() {
		$.ajax({
			url: '../clues/AL000526681025',
			type: "GET",
			dataType: "json",
			data: {},
			success: function (data) {
				alert(JSON.stringify(data));
			},
			error: function () { alert('get fail'); }
		});
	}

	function deleteClue() {
		$.ajax({
			url: '../clues',
			type: "DELETE",
			dataType: "text",
			data: {clueID: "AL000526681025", caseName: "1234567"},
			success: function (data) {
				alert(data);
			},
			error: function () { alert('delete fail'); }
		});
	}

	function postCaseClueRelation() {
		$.ajax({
			url: '../caseClueRelations',
			type: "POST",
			dataType: "text",
			data: {caseID: "AL000526681025", clueID: "abcdefg", relateTime: "2016/2/22 9:00"},
			success: function (data) {
				alert(data);
			},
			error: function () { alert('post fail'); }
		});
	}

	function putCaseClueRelation() {
		$.ajax({
			url: '../caseClueRelations',
			type: "PUT",
			dataType: "text",
			data: {caseID: "AL000526681025", clueID: "1234567", relateTime: "2015/3/1 0:00"},
			success: function (data) {
				alert(data);
			},
			error: function () { alert('put fail'); }
		});
	}

	function getCaseClueRelation() {
		$.ajax({
			url: '../caseClueRelations/AL000526681025',
			type: "GET",
			dataType: "json",
			data: {},
			success: function (data) {
				alert(JSON.stringify(data));
			},
			error: function () { alert('get fail'); }
		});
	}

	function deleteCaseClueRelation() {
		$.ajax({
			url: '../caseClueRelations',
			type: "DELETE",
			dataType: "text",
			data: {caseID: "AL000526681025", clueID: "1234567"},
			success: function (data) {
				alert(data);
			},
			error: function () { alert('delete fail'); }
		});
	}

	function postClueFileRelation() {
		$.ajax({
			url: '../clueFileRelations',
			type: "POST",
			dataType: "text",
			data: {clueID: "AL000526681025", fileID: "abcdefg", fileType: "1"},
			success: function (data) {
				alert(data);
			},
			error: function () { alert('post fail'); }
		});
	}

	function putClueFileRelation() {
		$.ajax({
			url: '../clueFileRelations',
			type: "PUT",
			dataType: "text",
			data: {clueID: "AL000526681025", fileID: "abcdefg", fileType: "2"},
			success: function (data) {
				alert(data);
			},
			error: function () { alert('put fail'); }
		});
	}

	function getClueFileRelation() {
		$.ajax({
			url: '../clueFileRelations/AL000526681025',
			type: "GET",
			dataType: "json",
			data: {},
			success: function (data) {
				alert(JSON.stringify(data));
			},
			error: function () { alert('get fail'); }
		});
	}

	function deleteClueFileRelation() {
		$.ajax({
			url: '../clueFileRelations',
			type: "DELETE",
			dataType: "text",
			data: {clueID: "AL000526681025", fileID: "abcdefg", fileType: "2"},
			success: function (data) {
				alert(data);
			},
			error: function () { alert('delete fail'); }
		});
	}

	function postVideo() {
		$.ajax({
			url: '../vio/videoslices',
			type: "POST",
			dataType: "text",
			contentType: "application/json; charset=utf-8",
			data: JSON.stringify([{videoID: "AL000526681025", clueID: "201508120900195003088", caseID: "A5201009708002013040009"}]),
			success: function (data) {
				alert(data);
			},
			error: function () { alert('post fail'); }
		});
	}

	function putVideo() {
		$.ajax({
			url: '../vio/videoslices',
			type: "PUT",
			dataType: "text",
			data: {videoID: "AL000526681025", videoSource:"科达", clueID: "201508120900195003088", caseID: "A5201009708002013040009"},
			success: function (data) {
				alert(data);
			},
			error: function () { alert('put fail'); }
		});
	}

	function getVideo() {
		$.ajax({
			url: '../vio/videoslices/AL000526681025',
			type: "GET",
			dataType: "json",
			data: {},
			success: function (data) {
				alert(JSON.stringify(data));
			},
			error: function () { alert('get fail'); }
		});
	}

	function deleteVideo() {
		$.ajax({
			url: '../vio/videoslices',
			type: "DELETE",
			dataType: "text",
			data: {videoID: "AL000526681025", clueID: "201508120900195003088", caseID: "A5201009708002013040009"},
			success: function (data) {
				alert(data);
			},
			error: function () { alert('delete fail'); }
		});
	}

	function postImage() {
		$.ajax({
			url: '../vio/images',
			type: "POST",
			dataType: "text",
			data: {imageID: "AL000526681025", clueID: "201508120900195003088", caseID: "A5201009708002013040009"},
			success: function (data) {
				alert(data);
			},
			error: function () { alert('post fail'); }
		});
	}

	function putImage() {
		$.ajax({
			url: '../vio/images',
			type: "PUT",
			dataType: "text",
			data: {imageID: "AL000526681025", imageSource:"科达", clueID: "201508120900195003088", caseID: "A5201009708002013040009"},
			success: function (data) {
				alert(data);
			},
			error: function () { alert('put fail'); }
		});
	}

	function getImage() {
		$.ajax({
			url: '../vio/images/AL000526681025',
			type: "GET",
			dataType: "json",
			data: {},
			success: function (data) {
				alert(JSON.stringify(data));
			},
			error: function () { alert('get fail'); }
		});
	}

	function deleteImage() {
		$.ajax({
			url: '../vio/images',
			type: "DELETE",
			dataType: "text",
			data: {imageID: "AL000526681025", clueID: "201508120900195003088", caseID: "A5201009708002013040009"},
			success: function (data) {
				alert(data);
			},
			error: function () { alert('delete fail'); }
		});
	}

	function postFile() {
		$.ajax({
			url: '../vio/files',
			type: "POST",
			dataType: "text",
			data: {fileID: "AL000526681025", clueID: "201508120900195003088", caseID: "A5201009708002013040009"},
			success: function (data) {
				alert(data);
			},
			error: function () { alert('post fail'); }
		});
	}

	function putFile() {
		$.ajax({
			url: '../vio/files',
			type: "PUT",
			dataType: "text",
			data: {fileID: "AL000526681025", source:"科达", clueID: "201508120900195003088", caseID: "A5201009708002013040009"},
			success: function (data) {
				alert(data);
			},
			error: function () { alert('put fail'); }
		});
	}

	function getFile() {
		$.ajax({
			url: '../vio/files/AL000526681025',
			type: "GET",
			dataType: "json",
			data: {},
			success: function (data) {
				alert(JSON.stringify(data));
			},
			error: function () { alert('get fail'); }
		});
	}

	function deleteFile() {
		$.ajax({
			url: '../vio/files',
			type: "DELETE",
			dataType: "text",
			data: {fileID: "AL000526681025", clueID: "201508120900195003088", caseID: "A5201009708002013040009"},
			success: function (data) {
				alert(data);
			},
			error: function () { alert('delete fail'); }
		});
	}

    function postEquipment() {
        $.ajax({
            url: '../equipments',
            type: "POST",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify([{"sbbh": "abcdefg", "sbmc": "测试新增设备1", "sblx": "5","sbtxfs":"0","sjqx":"0","sbshzt":"3", "qysj": "2016-09-22"},
                {"sbbh": "1234567", "sbmc": "测试新增设备2","sblx": "5","sbtxfs":"0","sjqx":"0","sbshzt":"3", "qysj": "2016-09-22 13:00:00"}]),
            success: function (data) {
                alert(JSON.stringify(data));
            },
            error: function (data) {
                alert('post fail');
			}
        });
    }

    function putEquipment() {
        $.ajax({
            url: '../equipments',
            type: "PUT",
            dataType: "text",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify([{"sbbh": "abcdefg", "sbmc": "测试更改设备1"},
                {"sbbh": "1234567", "sbmc": "测试更改设备2"}]),
            success: function (data) {
                alert(data);
            },
            error: function () { alert('put fail'); }
        });
    }

    function getEquipment() {
        $.ajax({
            url: '../equipments',
            type: "GET",
            dataType: "json",
            data: {sbbh: "1234567"},
            success: function (data) {
                alert(JSON.stringify(data));
            },
            error: function () { alert('get fail'); }
        });
    }

    function deleteEquipment() {
        $.ajax({
            url: '../equipments',
            type: "DELETE",
            dataType: "text",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify(["abcdefg", "1234567"]),
            success: function (data) {
                alert(data);
            },
            error: function () { alert('delete fail'); }
        });
    }
	/*$(function () {
	    //$.getJSON("http://127.0.0.1:8080../cases", function (data) {alert(data); });
	    $.ajax({
	        url: '../persons',
	        type: "POST",
	        dataType: "text",	        
	        data: {personID: "0201407241504456690902", sourceID: "abcdefg"},
	        success: function (data) {
	            alert(data);
	        },
	        error: function () { alert('post ajax fail'); }
	    });
	    
	    $.ajax({
	        url: '../persons',
	        type: "PUT",
	        dataType: "text",	        
	        data: {personID: "0201407241504456690902", sourceID: "1234567"},
	        success: function (data) {
	            alert(data);
	        },
	        error: function () { alert('post ajax fail'); }
	    });
	    
	    $.ajax({
	        url: '../persons/0201407241504456690902',
	        type: "GET",
	        dataType: "json",	        
	        data: {},
	        success: function (data) {
	            alert(JSON.stringify(data));
	        },
	        error: function () { alert('get ajax fail'); }
	    });
	    
	    $.ajax({
	        url: '../persons/0201407241504456690902',
	        type: "DELETE",
	        dataType: "text",	        
	        data: {},
	        success: function (data) {
	            alert(data);
	        },
	        error: function () { alert('delete ajax fail'); }
	    });
	});*/
</script> 
</body>
</html>