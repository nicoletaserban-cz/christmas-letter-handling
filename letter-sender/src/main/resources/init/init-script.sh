#!/bin/bash
topic_name="letter-topic"

awslocal sns create-topic --name $topic_name