import { entityItemSelector } from '../../support/commands';
import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityCreateCancelButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('StockExchange e2e test', () => {
  const stockExchangePageUrl = '/stock-exchange';
  const stockExchangePageUrlPattern = new RegExp('/stock-exchange(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const stockExchangeSample = { name: 'Orchestrator Cambridgeshire turquoise' };

  let stockExchange: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/services/watcher/api/stock-exchanges+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/services/watcher/api/stock-exchanges').as('postEntityRequest');
    cy.intercept('DELETE', '/services/watcher/api/stock-exchanges/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (stockExchange) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/services/watcher/api/stock-exchanges/${stockExchange.id}`,
      }).then(() => {
        stockExchange = undefined;
      });
    }
  });

  it('StockExchanges menu should load StockExchanges page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('stock-exchange');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('StockExchange').should('exist');
    cy.url().should('match', stockExchangePageUrlPattern);
  });

  describe('StockExchange page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(stockExchangePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create StockExchange page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/stock-exchange/new$'));
        cy.getEntityCreateUpdateHeading('StockExchange');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', stockExchangePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/services/watcher/api/stock-exchanges',
          body: stockExchangeSample,
        }).then(({ body }) => {
          stockExchange = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/services/watcher/api/stock-exchanges+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [stockExchange],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(stockExchangePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details StockExchange page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('stockExchange');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', stockExchangePageUrlPattern);
      });

      it('edit button click should load edit StockExchange page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('StockExchange');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', stockExchangePageUrlPattern);
      });

      it('last delete button click should delete instance of StockExchange', () => {
        cy.intercept('GET', '/services/watcher/api/stock-exchanges/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('stockExchange').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', stockExchangePageUrlPattern);

        stockExchange = undefined;
      });
    });
  });

  describe('new StockExchange page', () => {
    beforeEach(() => {
      cy.visit(`${stockExchangePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('StockExchange');
    });

    it('should create an instance of StockExchange', () => {
      cy.get(`[data-cy="name"]`).type('Analyst Implemented').should('have.value', 'Analyst Implemented');

      cy.get(`[data-cy="symbol"]`).type('e-services Glen').should('have.value', 'e-services Glen');

      cy.get(`[data-cy="country"]`).type('Sweden').should('have.value', 'Sweden');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        stockExchange = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', stockExchangePageUrlPattern);
    });
  });
});
