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

describe('StockSplitHistory e2e test', () => {
  const stockSplitHistoryPageUrl = '/stock-split-history';
  const stockSplitHistoryPageUrlPattern = new RegExp('/stock-split-history(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const stockSplitHistorySample = { date: '2022-03-28' };

  let stockSplitHistory: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/services/watcher/api/stock-split-histories+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/services/watcher/api/stock-split-histories').as('postEntityRequest');
    cy.intercept('DELETE', '/services/watcher/api/stock-split-histories/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (stockSplitHistory) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/services/watcher/api/stock-split-histories/${stockSplitHistory.id}`,
      }).then(() => {
        stockSplitHistory = undefined;
      });
    }
  });

  it('StockSplitHistories menu should load StockSplitHistories page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('stock-split-history');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('StockSplitHistory').should('exist');
    cy.url().should('match', stockSplitHistoryPageUrlPattern);
  });

  describe('StockSplitHistory page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(stockSplitHistoryPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create StockSplitHistory page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/stock-split-history/new$'));
        cy.getEntityCreateUpdateHeading('StockSplitHistory');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', stockSplitHistoryPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/services/watcher/api/stock-split-histories',
          body: stockSplitHistorySample,
        }).then(({ body }) => {
          stockSplitHistory = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/services/watcher/api/stock-split-histories+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/services/watcher/api/stock-split-histories?page=0&size=20>; rel="last",<http://localhost/services/watcher/api/stock-split-histories?page=0&size=20>; rel="first"',
              },
              body: [stockSplitHistory],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(stockSplitHistoryPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details StockSplitHistory page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('stockSplitHistory');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', stockSplitHistoryPageUrlPattern);
      });

      it('edit button click should load edit StockSplitHistory page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('StockSplitHistory');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', stockSplitHistoryPageUrlPattern);
      });

      it('last delete button click should delete instance of StockSplitHistory', () => {
        cy.intercept('GET', '/services/watcher/api/stock-split-histories/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('stockSplitHistory').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', stockSplitHistoryPageUrlPattern);

        stockSplitHistory = undefined;
      });
    });
  });

  describe('new StockSplitHistory page', () => {
    beforeEach(() => {
      cy.visit(`${stockSplitHistoryPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('StockSplitHistory');
    });

    it('should create an instance of StockSplitHistory', () => {
      cy.get(`[data-cy="date"]`).type('2022-03-28').should('have.value', '2022-03-28');

      cy.get(`[data-cy="ratio"]`).type('97841').should('have.value', '97841');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        stockSplitHistory = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', stockSplitHistoryPageUrlPattern);
    });
  });
});
