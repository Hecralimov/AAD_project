COMPOSE ?= docker compose

.PHONY: help up down restart restart-db restart-backend restart-frontend rebuild rebuild-db rebuild-backend rebuild-frontend logs ps

help:
	@echo "Available targets:"
	@echo "  make up                 Start database, backend, and frontend"
	@echo "  make down               Stop all services"
	@echo "  make restart            Restart database, backend, and frontend"
	@echo "  make restart-db         Restart database, then backend and frontend"
	@echo "  make restart-backend    Restart backend, then frontend"
	@echo "  make restart-frontend   Restart frontend only"
	@echo "  make rebuild            Rebuild and restart all services"
	@echo "  make rebuild-backend    Rebuild backend, then restart frontend"
	@echo "  make rebuild-frontend   Rebuild frontend only"
	@echo "  make logs               Follow logs for all services"
	@echo "  make ps                 Show service status"

up:
	$(COMPOSE) up -d mysql-db backend frontend

down:
	$(COMPOSE) down

restart:
	$(COMPOSE) restart mysql-db
	$(COMPOSE) up -d backend frontend
	$(COMPOSE) restart backend frontend

restart-db:
	$(COMPOSE) restart mysql-db
	$(COMPOSE) up -d backend frontend
	$(COMPOSE) restart backend frontend

restart-backend:
	$(COMPOSE) up -d mysql-db
	$(COMPOSE) restart backend
	$(COMPOSE) restart frontend

restart-frontend:
	$(COMPOSE) restart frontend

rebuild:
	$(COMPOSE) up -d --build mysql-db backend frontend

rebuild-db:
	$(COMPOSE) up -d mysql-db
	$(COMPOSE) restart backend frontend

rebuild-backend:
	$(COMPOSE) up -d mysql-db
	$(COMPOSE) up -d --build backend
	$(COMPOSE) restart frontend

rebuild-frontend:
	$(COMPOSE) up -d --build frontend

logs:
	$(COMPOSE) logs -f mysql-db backend frontend

ps:
	$(COMPOSE) ps
