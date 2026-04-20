@echo off
echo Installing Chart.js and dependencies...
call npm install chart.js ng2-charts

echo Generating components...
call npx ng generate component components/login --skip-tests
call npx ng generate component components/admin-dashboard --skip-tests
call npx ng generate component components/corporate-dashboard --skip-tests
call npx ng generate component components/individual-dashboard --skip-tests
call npx ng generate component components/chat-widget --skip-tests

echo Generating services...
call npx ng generate service services/auth --skip-tests
call npx ng generate service services/api --skip-tests

echo Done!
