#!/bin/bash
awslocal dynamodb put-item --table-name Posts --item '{
    "id": {"S": "POST1"},
    "authorId": {"S": "alex1"},
    "type": {"S": "PROJECT"},
    "status": {"S": "NEW"},
    "title": {"S": "Enhancing Mental Health Awareness"},
    "shortDescription": {"S": "Join me to improve mental health resources for students."},
    "description": {"S": "Looking for team members to build a mental health app."},
    "likeNumber": {"N": "0"},
    "commentNumber": {"N": "0"},
    "hashtags": {"L":  [{ "S" : "mentalhealth" }, { "S" : "startup" }, { "S" : "collaboration" }]},
    "createdDateTime": {"S": "2024-04-10T12:00:00Z"},
    "edited": {"BOOL": false}
}'

awslocal dynamodb put-item --table-name Posts --item '{
    "id": {"S": "comment1"},
    "postId": {"S": "POST1"},
    "authorId": {"S": "user2"},
    "content": {"S": "This is a great initiative, Alex! I am interested in helping."},
    "createdDateTime": {"S": "2024-04-10T13:00:00Z"},
    "likeNumber": {"N": "0"}
}'

awslocal dynamodb put-item --table-name Posts --item '{
    "id": {"S": "like1"},
    "targetType": {"S": "POST"},
    "targetId": {"S": "POST1"},
    "userId": {"S": "user3"},
    "timestamp": {"S": "2024-04-10T13:05:00Z"}
}'

awslocal dynamodb update-item --table-name Posts --key '{"id": {"S": "POST1"}}' --update-expression "SET likeNumber = likeNumber + :val1, commentNumber = commentNumber + :val2" --expression-attribute-values '{":val1":{"N":"1"}, ":val2":{"N":"1"}}'


awslocal dynamodb put-item --table-name Posts --item '{
    "id": {"S": "POST2"},
    "authorId": {"S": "user4"},
    "type": {"S": "POST"},
    "status": {"S": "NEW"},
    "title": {"S": "Looking for Physics Study Group Members"},
    "shortDescription": {"S": "Anyone interested in forming a study group for the upcoming Physics exam?"},
    "description": {"S": "Planning to meet twice a week to discuss topics and solve problems."},
    "likeNumber": {"N": "0"},
    "commentNumber": {"N": "0"},
    "hashtags": {"L": [{"S":"physics"}, {"S":"studygroup"}, {"S":"examprep"}]},
    "createdDateTime": {"S": "2024-04-11T09:00:00Z"},
    "edited": {"BOOL": false}
}'

awslocal dynamodb put-item --table-name Posts --item '{
    "id": {"S": "comment2"},
    "postId": {"S": "POST2"},
    "authorId": {"S": "user6"},
    "content": {"S": "Count me in! Struggling with the latest chapters."},
    "createdDateTime": {"S": "2024-04-11T10:30:00Z"},
    "likeNumber": {"N": "0"}
}'


awslocal dynamodb put-item --table-name Posts --item '{
    "id": {"S": "POST3"},
    "authorId": {"S": "user5"},
    "type": {"S": "POST"},
    "status": {"S": "NEW"},
    "title": {"S": "Join Our Campus Clean-Up Drive"},
    "shortDescription": {"S": "Let us make our campus greener and cleaner together!"},
    "description": {"S": "Meeting point at the main library, supplies will be provided."},
    "likeNumber": {"N": "0"},
    "commentNumber": {"N": "0"},
    "hashtags": {"L": [{"S": "environment"}, {"S": "cleanup"}, {"S": "volunteering"}]},
    "createdDateTime": {"S": "2024-04-12T08:00:00Z"},
    "edited": {"BOOL": false}
}'

awslocal dynamodb put-item --table-name Posts --item '{
    "id": {"S": "comment3"},
    "postId": {"S": "POST3"},
    "authorId": {"S": "user7"},
    "content": {"S": "Great initiative! I will be there and bring a couple of friends."},
    "createdDateTime": {"S": "2024-04-12T09:15:00Z"},
    "likeNumber": {"N": "0"}
}'

awslocal dynamodb put-item --table-name Posts --item '{
    "id": {"S": "like2"},
    "targetType": {"S": "post"},
    "targetId": {"S": "POST2"},
    "userId": {"S": "user8"},
    "timestamp": {"S": "2024-04-11T11:00:00Z"}
}'

awslocal dynamodb put-item --table-name Posts --item '{
    "id": {"S": "like3"},
    "targetType": {"S": "comment"},
    "targetId": {"S": "comment3"},
    "userId": {"S": "user9"},
    "timestamp": {"S": "2024-04-12T09:30:00Z"}
}'

awslocal dynamodb update-item --table-name Posts --key '{"id": {"S": "POST2"}}' --update-expression "SET likeNumber = likeNumber + :val1, commentNumber = commentNumber + :val1" --expression-attribute-values '{":val1":{"N":"1"}}'

awslocal dynamodb update-item --table-name Posts --key '{"id": {"S": "POST3"}}' --update-expression "SET likeNumber = likeNumber + :val1, commentNumber = commentNumber + :val1" --expression-attribute-values '{":val1":{"N":"1"}}'

awslocal dynamodb put-item --table-name Posts --item '{
    "id": {"S": "like4"},
    "targetType": {"S": "comment"},
    "targetId": {"S": "comment1"},
    "userId": {"S": "user10"},
    "timestamp": {"S": "2024-04-10T14:00:00Z"}
}'

awslocal dynamodb put-item --table-name Posts --item '{
    "id": {"S": "like5"},
    "targetType": {"S": "comment"},
    "targetId": {"S": "comment2"},
    "userId": {"S": "user11"},
    "timestamp": {"S": "2024-04-11T12:00:00Z"}
}'

awslocal dynamodb update-item --table-name Posts --key '{"id": {"S": "comment1"}}' --update-expression "SET likeNumber = likeNumber + :val" --expression-attribute-values '{":val":{"N":"1"}}'
awslocal dynamodb update-item --table-name Posts --key '{"id": {"S": "comment2"}}' --update-expression "SET likeNumber = likeNumber + :val" --expression-attribute-values '{":val":{"N":"1"}}'

awslocal dynamodb put-item --table-name Posts --item '{
    "id": {"S": "comment4"},
    "postId": {"S": "POST3"},
    "authorId": {"S": "user13"},
    "content": {"S": "Super excited about this event! I have always been passionate about environmental causes."},
    "createdDateTime": {"S": "2024-04-12T15:00:00Z"},
    "likeNumber": {"N": "0"}
}'

awslocal dynamodb update-item --table-name Posts --key '{"id": {"S": "POST3"}}' --update-expression "SET commentNumber = commentNumber + :val" --expression-attribute-values '{":val":{"N":"1"}}'

